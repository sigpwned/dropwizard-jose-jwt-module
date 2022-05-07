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

import java.io.IOException;
import java.time.Duration;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;


public interface JWTFactory {
  public static final JWSAlgorithm DEFAULT_SIGNING_ALGORITHM = JWSAlgorithm.RS256;

  SignedJWT create(JWTClaimsSet claims) throws IOException;

  /**
   * @return the jwks
   */
  JWKSet getJwks();

  /**
   * @return the realm
   */
  String getIssuer();

  /**
   * @return the duration
   */
  Duration getTokenLifetime();

  /**
   * @return the algorithm
   */
  JWSAlgorithm getSigningAlgorithm();

}
