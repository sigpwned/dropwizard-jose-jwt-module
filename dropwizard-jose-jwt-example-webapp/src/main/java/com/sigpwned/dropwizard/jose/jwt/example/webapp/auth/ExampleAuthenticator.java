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
package com.sigpwned.dropwizard.jose.jwt.example.webapp.auth;

import java.text.ParseException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.nimbusds.jwt.JWTClaimsSet;
import com.sigpwned.dropwizard.jose.jwt.example.webapp.model.Account;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;

/**
 * Because we are using stateless JWTs, this object is basically just a mapping from JWT claims to
 * our application's specific user model object, {@link Account}.
 */
public class ExampleAuthenticator implements Authenticator<JWTClaimsSet, Account> {
  private static final Logger LOGGER = LoggerFactory.getLogger(ExampleAuthenticator.class);

  /**
   * The underlying JWT has already been parsed and verified by the time we reach this code, so you
   * can trust the claims.
   */
  @Override
  public Optional<Account> authenticate(JWTClaimsSet claims) throws AuthenticationException {
    try {
      // The whole point of stateless JWTs is that we put all of the required session information
      // into the JWT itself as claims. So let's just grab those claims and make our user!
      String id = claims.getStringClaim("accountId");
      String username = claims.getStringClaim("accountUsername");
      String name = claims.getStringClaim("accountName");
      return Optional.of(Account.of(id, username, name));
    } catch (ParseException e) {
      // Well, that is exceptionally odd. We have a JWT that has been verified as coming from us --
      // or, at least, from soneome who knows our private key -- but it does not contain the
      // required claims. That is an application bug at best, and indicates a compromised private
      // key at worst.
      if (LOGGER.isErrorEnabled())
        LOGGER.error("Failed to create user from verified JWT. Compromised private key? claims={}",
            claims);
      throw new AuthenticationException("Valid JWT does not contain required claims");
    }
  }
}
