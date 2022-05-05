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
import com.nimbusds.jwt.JWTClaimsSet;
import com.sigpwned.dropwizard.jose.jwt.util.JWKSets;
import com.sigpwned.dropwizard.jose.jwt.util.KeyStores;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.Authorizer;
import io.dropwizard.core.ConfiguredBundle;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;

public class JWTBundle<P extends Principal> implements ConfiguredBundle<JWTBundleConfiguration> {
  public static class Builder<P extends Principal> {
    private Authenticator<JWTClaimsSet, P> authenticator;
    private Authorizer<P> authorizer;

    /**
     * @param authenticator the authenticator to set
     */
    public Builder<P> setAuthenticator(Authenticator<JWTClaimsSet, P> authenticator) {
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

  private final Authenticator<JWTClaimsSet, P> authenticator;
  private final Authorizer<P> authorizer;

  public JWTBundle(Authenticator<JWTClaimsSet, P> authenticator, Authorizer<P> authorizer) {
    this.authorizer = authorizer;
    this.authenticator = authenticator;
  }

  @Override
  public void initialize(Bootstrap<?> bootstrap) {
    // nop
  }

  @Override
  public void run(JWTBundleConfiguration bundleConfiguration, Environment environment)
      throws Exception {
    final JWTConfiguration configuration = bundleConfiguration.getJWTConfiguration();

    final JWTFactory jwtFactory = newJWTFactory(configuration);

    environment.jersey().register(new AbstractBinder() {
      @Override
      protected void configure() {
        bind(jwtFactory).to(JWTFactory.class);
      }
    });

    environment.jersey().register(new AuthDynamicFeature(JWTAuthFilter.<P>builder(jwtFactory)
        .setAuthenticator(authenticator).setAuthorizer(authorizer).buildAuthFilter()));

    environment.servlets()
        .addFilter("WellKnownJwks", new WellKnownJWKSetHttpFilter(jwtFactory.getJwks()))
        .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), false, "/*");
  }

  /**
   * Creates the JWT Factory from JWKs
   */
  public JWTFactory newJWTFactory(JWTConfiguration configuration) throws IOException {
    KeyStore store = loadKeyStore(configuration);

    JWKSet jwks;
    try {
      jwks = JWKSets.fromKeyStore(store);
    } catch (KeyStoreException e) {
      throw new IOException("Failed to load keys from store", e);
    }

    return new JWTFactory(jwks, configuration.getIssuer(), configuration.getTokenLifetime());
  }

  private KeyStore loadKeyStore(JWTConfiguration configuration) throws IOException {
    File keyStoreFile = new File(configuration.getKeyStorePath());
    if (!keyStoreFile.isFile())
      throw new FileNotFoundException(configuration.getKeyStorePath());
    return KeyStores.loadKeyStore(configuration.getKeyStoreType(), keyStoreFile,
        configuration.getKeyStorePassword(), configuration.getKeyStoreProvider());
  }
}
