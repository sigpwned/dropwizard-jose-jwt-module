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
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.time.Duration;
import org.hibernate.validator.constraints.time.DurationMin;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.sigpwned.dropwizard.jose.jwt.factory.DefaultJWTFactory;
import com.sigpwned.dropwizard.jose.jwt.util.KeyStores;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

public class JWTConfiguration {
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
   * The optional type of keystore. The default value is PKCS12. If you used the KeygenTool in this
   * repository, then you must use PKCS12.
   */
  @Valid
  @NotEmpty
  private String keyStoreType = KeyStores.DEFAULT_TYPE;

  /**
   * The optional crypto provider to use to read the key store. If no value is given, the the JDK
   * default provider will be used. Any other value must be provided by the application as a
   * dependency.
   */
  @Valid
  private String keyStoreProvider;

  /**
   * The algorithm used to sign new tokens. If you used the KeygenTool in this repository, then you
   * must use an RSA-based algorithm.
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
   * @return the keyStorePath
   */
  public String getKeyStorePath() {
    return keyStorePath;
  }

  /**
   * @param keyStorePath the keyStorePath to set
   */
  public void setKeyStorePath(String keyStorePath) {
    this.keyStorePath = keyStorePath;
  }

  /**
   * @return the keyStorePassword
   */
  public String getKeyStorePassword() {
    return keyStorePassword;
  }

  /**
   * @param keyStorePassword the keyStorePassword to set
   */
  public void setKeyStorePassword(String keyStorePassword) {
    this.keyStorePassword = keyStorePassword;
  }

  /**
   * @return the keyStoreType
   */
  public String getKeyStoreType() {
    return keyStoreType;
  }

  /**
   * @param keyStoreType the keyStoreType to set
   */
  public void setKeyStoreType(String keyStoreType) {
    this.keyStoreType = keyStoreType;
  }

  /**
   * @return the keyStoreProvider
   */
  public String getKeyStoreProvider() {
    return keyStoreProvider;
  }

  /**
   * @param keyStoreProvider the keyStoreProvider to set
   */
  public void setKeyStoreProvider(String keyStoreProvider) {
    this.keyStoreProvider = keyStoreProvider;
  }

  /**
   * @return the signingAlgorithm
   */
  public JWSAlgorithm getSigningAlgorithm() {
    return signingAlgorithm;
  }

  /**
   * @param signingAlgorithm the signingAlgorithm to set
   */
  public void setSigningAlgorithm(JWSAlgorithm signingAlgorithm) {
    this.signingAlgorithm = signingAlgorithm;
  }

  /**
   * @return the tokenLifetime
   */
  public Duration getTokenLifetime() {
    return tokenLifetime;
  }

  /**
   * @param tokenLifetime the tokenLifetime to set
   */
  public void setTokenLifetime(Duration tokenLifetime) {
    this.tokenLifetime = tokenLifetime;
  }

  /**
   * @return the issuer
   */
  public String getIssuer() {
    return issuer;
  }

  /**
   * @param issuer the issuer to set
   */
  public void setIssuer(String issuer) {
    this.issuer = issuer;
  }

  private JWTFactory jwtFactoryInstance;

  public synchronized JWTFactory buildJWTFactory() throws IOException {
    if (jwtFactoryInstance == null)
      jwtFactoryInstance = newJWTFactory();
    return jwtFactoryInstance;
  }

  private JWTFactory newJWTFactory() throws IOException {
    KeyStore store = KeyStores.loadKeyStore(getKeyStoreType(), new File(getKeyStorePath()),
        getKeyStorePassword(), getKeyStoreProvider());

    JWKSet jwks;
    try {
      jwks = JWKSet.load(store, null);
    } catch (KeyStoreException e) {
      throw new IOException("Failed to load keys from store", e);
    }

    return new DefaultJWTFactory(jwks, getIssuer(), getTokenLifetime());
  }
}
