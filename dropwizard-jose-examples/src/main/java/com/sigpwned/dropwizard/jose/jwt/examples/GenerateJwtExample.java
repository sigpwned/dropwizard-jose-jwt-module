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
package com.sigpwned.dropwizard.jose.jwt.examples;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.SignedJWT;
import com.sigpwned.dropwizard.jose.jwt.JWTFactory;

/**
 * Generates a JWT using an RSA256 Algorithm
 */
public class GenerateJwtExample {
  public static void main(String[] args) throws Exception {
    // JWKs are usually generated out of band and loaded from a KeyStore
    RSAKey jwk =
        new RSAKeyGenerator(2048).keyIDFromThumbprint(true).keyUse(KeyUse.SIGNATURE).generate();

    JWKSet jwks = new JWKSet(List.of(jwk));

    // Given JWKs, create our factory
    JWTFactory factory = new JWTFactory(jwks, "issuer", Duration.ofHours(1L));

    SignedJWT jwt = factory.create(Map.of("userId", "alpha", "userName", "Alpha Bravo"));

    System.out.println(jwt.serialize());
  }
}
