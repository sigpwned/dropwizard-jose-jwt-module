/*-
 * =================================LICENSE_START==================================
 * dropwizard-jose-jwt
 * ====================================SECTION=====================================
 * Copyright (C) 2022 Andy Boothe
 * ====================================SECTION=====================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ==================================LICENSE_END===================================
 */
package com.sigpwned.dropwizard.jose.jwt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.Principal;
import java.util.EnumSet;
import javax.servlet.DispatcherType;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jwt.SignedJWT;
import com.sigpwned.dropwizard.jose.jwt.factory.DefaultJWTFactory;
import com.sigpwned.dropwizard.jose.jwt.util.KeyStores;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.Authorizer;
import io.dropwizard.core.ConfiguredBundle;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;

/**
 * 
 * A configuration bundle for adding JWT features to a Dropwizard application.
 *
 * @param <P> The application {@link Principal} type
 */
public class JWTBundle<P extends Principal> implements ConfiguredBundle<JWTBundleConfiguration> {
  public static class Builder<P extends Principal> {
    private Authenticator<SignedJWT, P> authenticator;
    private Authorizer<P> authorizer;

    /**
     * @param authenticator the authenticator to set
     */
    public Builder<P> setAuthenticator(Authenticator<SignedJWT, P> authenticator) {
      this.authenticator = authenticator;
      return this;
    }

    /**
     * @param authorizer the authorizer to set
     */
    public Builder<P> setAuthorizer(Authorizer<P> authorizer) {
      this.authorizer = authorizer;
      return this;
    }

    public JWTBundle<P> buildJWTBundle() {
      return new JWTBundle<>(authenticator, authorizer);
    }
  }

  public static <P extends Principal> JWTBundle.Builder<P> builder() {
    return new Builder<>();
  }

  private final Authenticator<SignedJWT, P> authenticator;
  private final Authorizer<P> authorizer;

  public JWTBundle(Authenticator<SignedJWT, P> authenticator, Authorizer<P> authorizer) {
    this.authorizer = authorizer;
    this.authenticator = authenticator;
  }

  @Override
  public void initialize(Bootstrap<?> bootstrap) {
    // nop
  }

  /* default */ static final String WELL_KNOWN_JWKS_FILTER_NAME = "WellKnownJwks";

  @Override
  public void run(JWTBundleConfiguration bundleConfiguration, Environment environment)
      throws Exception {
    // Pull out our configuration
    final JWTConfiguration configuration = bundleConfiguration.getJWTConfiguration();

    // Build our JWT factory
    final JWTFactory jwtFactory = newJWTFactory(configuration);

    // Register our JWT factory for dependency injection
    environment.jersey().register(new AbstractBinder() {
      @Override
      protected void configure() {
        bind(jwtFactory).to(JWTFactory.class);
      }
    });

    // Register the auth filter that checks JWTs on the way in. Note that the JWTAuthFilter does NOT
    // issue new JWTs. That is up to the user. For a good example, see the example webapp project in
    // this repository.
    environment.jersey()
        .register(new AuthDynamicFeature(JWTAuthFilter.<P>builder()
            .setIssuer(jwtFactory.getIssuer()).setRealm(jwtFactory.getIssuer())
            .setJWKs(jwtFactory.getJwks()).setSigningAlgorithm(jwtFactory.getSigningAlgorithm())
            .setAuthenticator(authenticator).setAuthorizer(authorizer).buildAuthFilter()));

    // Register the servlet filter that makes JWK public key available for third party users. This
    // allows them to verify JWTs on their own. Note that we have to use a public key cryptosystem
    // (like RSA) as opposed to a symmetric key cryptosystem (like AES) for this to make sense.
    environment.servlets()
        .addFilter(WELL_KNOWN_JWKS_FILTER_NAME, new WellKnownJWKSetHttpFilter(jwtFactory.getJwks()))
        .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), false, "/*");
  }

  /**
   * Creates the JWT Factory from JWKs
   */
  /* default */ JWTFactory newJWTFactory(JWTConfiguration configuration) throws IOException {
    KeyStore store = loadKeyStore(configuration);

    JWKSet jwks;
    try {
      jwks = JWKSet.load(store, null);
    } catch (KeyStoreException e) {
      throw new IOException("Failed to load keys from store", e);
    }

    return new DefaultJWTFactory(jwks, configuration.getIssuer(), configuration.getTokenLifetime());
  }

  /**
   * Loads the keys for our JWKs from the configured key store
   */
  /* default */ KeyStore loadKeyStore(JWTConfiguration configuration) throws IOException {
    File keyStoreFile = new File(configuration.getKeyStorePath());
    if (!keyStoreFile.isFile())
      throw new FileNotFoundException(configuration.getKeyStorePath());
    return KeyStores.loadKeyStore(configuration.getKeyStoreType(), keyStoreFile,
        configuration.getKeyStorePassword(), configuration.getKeyStoreProvider());
  }
}
