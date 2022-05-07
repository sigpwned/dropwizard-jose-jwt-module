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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.security.KeyStore;
import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.mint.ConfigurableJWSMinter;
import com.nimbusds.jose.mint.DefaultJWSMinter;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.sigpwned.dropwizard.jose.jwt.tool.keygen.KeygenTool;
import com.sigpwned.dropwizard.jose.jwt.tool.keygen.KeygenToolConfiguration;
import com.sigpwned.dropwizard.jose.jwt.util.KeyStores;

public class JWTFactoryTest {
  public File keyStoreFile;
  public KeyStore keyStore;
  public JWKSet jwks;

  public static final String PASSWORD = "password";

  @Before
  public void setupJWTFactoryTest() throws Exception {
    keyStoreFile = File.createTempFile("keystore.", ".p12");

    KeygenToolConfiguration ktc = new KeygenToolConfiguration();
    ktc.realm = "realm";
    ktc.password = PASSWORD;
    ktc.out = new PrintStream(new FileOutputStream(keyStoreFile));

    KeygenTool.main(ktc);

    keyStore = KeyStores.loadKeyStore(KeyStores.DEFAULT_TYPE, keyStoreFile, PASSWORD);

    jwks = JWKSet.load(keyStore, null);
  }

  @After
  public void cleanupJWTFactoryTest() {
    keyStoreFile.delete();
  }

  @Test
  public void shouldCreateJWTProperly() throws Exception {
    final Instant now = Instant.now();
    final String issuer = "example";
    final Duration tokenLifetime = Duration.ofHours(1L);
    final JWSAlgorithm signingAlgorithm = JWTFactory.DEFAULT_SIGNING_ALGORITHM;
    final String jwtID = "hello";
    final JWTClaimsSet claims = new JWTClaimsSet.Builder().claim("alpha", "bravo").build();

    JWTFactory unit = new JWTFactory(jwks, issuer, tokenLifetime, signingAlgorithm) {
      @Override
      protected Instant now() {
        return now;
      }

      @Override
      protected String generateJwtID() {
        return jwtID;
      }
    };

    ConfigurableJWSMinter<SecurityContext> minter = new DefaultJWSMinter<>();
    minter.setJWKSource(new ImmutableJWKSet<>(jwks));

    JWSHeader header = new JWSHeader.Builder(signingAlgorithm).build();

    JWTClaimsSet.Builder csb = new JWTClaimsSet.Builder().issuer(issuer).jwtID(jwtID)
        .issueTime(Date.from(now)).expirationTime(Date.from(now.plus(tokenLifetime)));
    for (Map.Entry<String, Object> claim : claims.getClaims().entrySet()) {
      csb.claim(claim.getKey(), claim.getValue());
    }

    SignedJWT expected;
    try {
      JWSObject jws = minter.mint(header, csb.build().toPayload(), null);
      expected = SignedJWT.parse(jws.serialize());
    } catch (ParseException | JOSEException e) {
      throw new IOException("Failed to generate signed JWT", e);
    }

    SignedJWT observed = unit.create(claims);

    assertThat(expected.serialize(), is(observed.serialize()));
  }
}
