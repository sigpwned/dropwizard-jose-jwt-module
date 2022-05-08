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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.ignoreStubs;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.security.KeyStore;
import java.security.Principal;
import java.util.EnumSet;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jwt.JWTClaimsSet;
import com.sigpwned.dropwizard.jose.jwt.tool.keygen.KeygenTool;
import com.sigpwned.dropwizard.jose.jwt.tool.keygen.KeygenToolConfiguration;
import com.sigpwned.dropwizard.jose.jwt.util.KeyStores;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.Authorizer;
import io.dropwizard.core.setup.Environment;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.jetty.setup.ServletEnvironment;

public class JWTBundleTest {
  public class ExamplePrincipal implements Principal {
    @Override
    public String getName() {
      return "John Smith";
    }
  }

  public File keyStoreFile;
  public KeyStore keyStore;

  public static final String ISSUER = "issuer";

  public static final String PASSWORD = "password";

  @Before
  public void setupJWTFactoryTest() throws Exception {
    keyStoreFile = File.createTempFile("keystore.", ".p12");

    KeygenToolConfiguration ktc = new KeygenToolConfiguration();
    ktc.realm = "realm";
    ktc.password = PASSWORD;
    ktc.out = new PrintStream(new FileOutputStream(keyStoreFile));

    KeygenTool.main(ktc);

    keyStore = KeyStores.loadKeyStore(keyStoreFile, PASSWORD);
  }

  @After
  public void cleanupJWTFactoryTest() {
    keyStoreFile.delete();
  }

  @Test
  public void shouldLoadJWTFeaturesProperly() throws Exception {
    @SuppressWarnings("unchecked")
    final Authenticator<JWTClaimsSet, ExamplePrincipal> authenticator = mock(Authenticator.class);

    @SuppressWarnings("unchecked")
    final Authorizer<ExamplePrincipal> authorizer = mock(Authorizer.class);

    JWTConfiguration configuration = new JWTConfiguration();
    configuration.setIssuer(ISSUER);
    configuration.setKeyStorePassword(PASSWORD);
    configuration.setKeyStorePath(keyStoreFile.getAbsolutePath());
    configuration.setKeyStoreType(KeygenTool.KEY_STORE_TYPE);
    configuration.setSigningAlgorithm(JWSAlgorithm.RS256);

    JWTBundleConfiguration bundleConfiguration = mock(JWTBundleConfiguration.class);
    when(bundleConfiguration.getJWTConfiguration()).thenReturn(configuration);

    final JWTBundle<ExamplePrincipal> unit =
        new JWTBundle<ExamplePrincipal>(authenticator, authorizer);

    JerseyEnvironment jerseyEnvironment = mock(JerseyEnvironment.class);

    FilterRegistration.Dynamic filterRegistrationDynamic = mock(FilterRegistration.Dynamic.class);

    ServletEnvironment servletEnvironment = mock(ServletEnvironment.class);
    when(servletEnvironment.addFilter(eq(JWTBundle.WELL_KNOWN_JWKS_FILTER_NAME), any(Filter.class)))
        .thenReturn(filterRegistrationDynamic);

    Environment environment = mock(Environment.class);
    when(environment.jersey()).thenReturn(jerseyEnvironment);
    when(environment.servlets()).thenReturn(servletEnvironment);

    unit.run(bundleConfiguration, environment);

    verify(jerseyEnvironment).register(any(AbstractBinder.class));
    verify(jerseyEnvironment).register(any(AuthDynamicFeature.class));

    verify(filterRegistrationDynamic).addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST),
        false, "/*");

    verifyNoMoreInteractions(ignoreStubs(environment, jerseyEnvironment, servletEnvironment));
  }
}
