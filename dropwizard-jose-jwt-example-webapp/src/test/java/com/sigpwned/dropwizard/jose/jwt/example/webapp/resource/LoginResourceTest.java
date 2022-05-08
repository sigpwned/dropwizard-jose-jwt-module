/*-
 * =================================LICENSE_START==================================
 * dropwizard-jose-jwt-example-webapp
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
package com.sigpwned.dropwizard.jose.jwt.example.webapp.resource;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.security.KeyStore;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import javax.ws.rs.NotAuthorizedException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jwt.JWTClaimsSet;
import com.sigpwned.dropwizard.jose.jwt.JWTFactory;
import com.sigpwned.dropwizard.jose.jwt.example.webapp.AccountStore;
import com.sigpwned.dropwizard.jose.jwt.example.webapp.model.Account;
import com.sigpwned.dropwizard.jose.jwt.example.webapp.model.NewSession;
import com.sigpwned.dropwizard.jose.jwt.example.webapp.util.Claims;
import com.sigpwned.dropwizard.jose.jwt.factory.DefaultJWTFactory;
import com.sigpwned.dropwizard.jose.jwt.tool.keygen.KeygenTool;
import com.sigpwned.dropwizard.jose.jwt.tool.keygen.KeygenToolConfiguration;
import com.sigpwned.dropwizard.jose.jwt.util.KeyStores;

public class LoginResourceTest {
  public File keyStoreFile;
  public KeyStore keyStore;
  public JWKSet jwks;

  public static final String PASSWORD = "password";

  public static final String ISSUER = "issuer";

  @Before
  public void setupJWTFactoryTest() throws Exception {
    keyStoreFile = File.createTempFile("keystore.", ".p12");

    KeygenToolConfiguration ktc = new KeygenToolConfiguration();
    ktc.realm = "realm";
    ktc.password = PASSWORD;
    ktc.out = new PrintStream(new FileOutputStream(keyStoreFile));

    KeygenTool.main(ktc);

    keyStore = KeyStores.loadKeyStore(keyStoreFile, PASSWORD);

    jwks = JWKSet.load(keyStore, null);
  }

  @After
  public void cleanupJWTFactoryTest() {
    keyStoreFile.delete();
  }

  @Test
  public void shouldReturnSessionIfCredentialsAreValid() throws IOException {
    final String id = "id";
    final String username = "username";
    final String password = "password";
    final String name = "User Name";

    final Account account = Account.of(id, username, name);

    final JWTClaimsSet claims = new JWTClaimsSet.Builder().claim(Claims.ACCOUNT_ID_CLAIM, id)
        .claim(Claims.ACCOUNT_USERNAME_CLAIM, username).claim(Claims.ACCOUNT_NAME_CLAIM, name)
        .build();

    AccountStore accountStore = mock(AccountStore.class);
    when(accountStore.authenticate(username, password)).thenReturn(Optional.of(account));

    final Instant now = Instant.now();
    final String jwtId = "jwtid";

    JWTFactory jwtFactory = new DefaultJWTFactory(jwks, ISSUER, Duration.ofHours(1L)) {
      @Override
      protected Instant now() {
        return now;
      }

      @Override
      protected String generateJwtID() {
        return jwtId;
      }
    };

    LoginResource unit = new LoginResource(accountStore, jwtFactory);

    NewSession observed = unit.login(username, password);

    NewSession expected = NewSession.of(jwtFactory.create(claims), account);

    assertThat(observed, is(expected));
  }

  @Test(expected = NotAuthorizedException.class)
  public void shouldFailWithUnauthorizedIfCredentialsAreNotValid() throws IOException {
    final String username = "username";
    final String password = "password";

    AccountStore accountStore = mock(AccountStore.class);
    when(accountStore.authenticate(username, password)).thenReturn(Optional.empty());

    JWTFactory jwtFactory = mock(JWTFactory.class);

    LoginResource unit = new LoginResource(accountStore, jwtFactory);

    unit.login(username, password);
  }
}
