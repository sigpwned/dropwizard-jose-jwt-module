/*-
 * =================================LICENSE_START==================================
 * dropwizard-jwt
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
import java.time.Duration;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.time.DurationMin;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.sigpwned.dropwizard.jose.jwt.util.JWKSets;
import com.sigpwned.dropwizard.jose.jwt.util.KeyStores;

public class JOSEFactory {
  /**
   * The local filepath where the key store can be found
   */
  @Valid
  @NotEmpty
  private String keyStorePath;

  /**
   * The password to decrypt the keystore. Individual keys must not be encrypted.
   */
  @Valid
  @NotEmpty
  private String keyStorePassword;

  /**
   * The optional type of keystore. The default value is PKCS12.
   */
  @Valid
  @NotEmpty
  private String keyStoreType = KeyStores.DEFAULT_TYPE;

  /**
   * The optional crypto provider to use to read the key store. If no value is given, the the JDK
   * default provider will be used.
   */
  @Valid
  private String keyStoreProvider;

  /**
   * The algorithm used to sign new tokens
   */
  @Valid
  private JWSAlgorithm signingAlgorithm = JWTFactory.DEFAULT_SIGNING_ALGORITHM;

  /**
   * How long a token will remain valid before expiring? Must be positive.
   */
  @Valid
  @DurationMin(nanos = 0, inclusive = false)
  private Duration tokenLifetime = Duration.ofHours(1L);

  /**
   * The issuer to use for the tokens. In general, this should be set to the domain name of the
   * application.
   */
  @Valid
  @NotEmpty
  private String issuer;

  /**
   * Creates the JWT Factory from JWKs
   */
  public JWTFactory build() throws IOException {
    KeyStore store = loadKeyStore();

    JWKSet jwks;
    try {
      jwks = JWKSets.fromKeyStore(store);
    } catch (KeyStoreException e) {
      throw new IOException("Failed to load keys from store", e);
    }

    return new JWTFactory(jwks, issuer, tokenLifetime);
  }

  private KeyStore loadKeyStore() throws IOException {
    File keyStoreFile = new File(keyStorePath);
    if (!keyStoreFile.isFile())
      throw new FileNotFoundException(keyStorePath);
    return KeyStores.loadKeyStore(keyStoreType, keyStoreFile, keyStorePassword, keyStoreProvider);
  }
}
