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
import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
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

public class JWTFactory {
  public static final JWSAlgorithm DEFAULT_SIGNING_ALGORITHM = JWSAlgorithm.RS256;

  private final JWKSet jwks;
  private final String issuer;
  private final Duration tokenLifetime;
  private final JWSAlgorithm signingAlgorithm;

  public JWTFactory(JWKSet jwks, String issuer, Duration tokenLifetime) {
    this(jwks, issuer, tokenLifetime, DEFAULT_SIGNING_ALGORITHM);
  }

  public JWTFactory(JWKSet jwks, String issuer, Duration tokenLifetime,
      JWSAlgorithm signingAlgorithm) {
    this.jwks = jwks;
    this.issuer = issuer;
    this.tokenLifetime = tokenLifetime;
    this.signingAlgorithm = signingAlgorithm;
  }

  public SignedJWT create() throws IOException {
    return create(new JWTClaimsSet.Builder().build());
  }

  public SignedJWT create(JWTClaimsSet claims) throws IOException {
    final Instant now = now();

    ConfigurableJWSMinter<SecurityContext> minter = new DefaultJWSMinter<>();

    minter.setJWKSource(new ImmutableJWKSet<>(getJwks()));

    JWSHeader header = new JWSHeader.Builder(getSigningAlgorithm()).build();

    JWTClaimsSet.Builder csb =
        new JWTClaimsSet.Builder().issuer(getIssuer()).jwtID(UUID.randomUUID().toString())
            .issueTime(Date.from(now)).expirationTime(Date.from(now.plus(getTokenLifetime())));
    for (Map.Entry<String, Object> claim : claims.getClaims().entrySet()) {
      csb.claim(claim.getKey(), claim.getValue());
    }

    SignedJWT result;
    try {
      JWSObject jws = minter.mint(header, csb.build().toPayload(), null);
      result = SignedJWT.parse(jws.serialize());
    } catch (ParseException | JOSEException e) {
      throw new IOException("Failed to generate signed JWT", e);
    }

    return result;
  }

  /**
   * @return the jwks
   */
  public JWKSet getJwks() {
    return jwks;
  }

  /**
   * @return the realm
   */
  public String getIssuer() {
    return issuer;
  }

  /**
   * @return the duration
   */
  public Duration getTokenLifetime() {
    return tokenLifetime;
  }

  /**
   * @return the algorithm
   */
  public JWSAlgorithm getSigningAlgorithm() {
    return signingAlgorithm;
  }

  /**
   * test hook
   */
  protected Instant now() {
    return Instant.now();
  }
}
