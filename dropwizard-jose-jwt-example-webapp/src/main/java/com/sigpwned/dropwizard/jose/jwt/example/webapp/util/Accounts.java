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
package com.sigpwned.dropwizard.jose.jwt.example.webapp.util;

import java.text.ParseException;
import java.util.Optional;
import com.nimbusds.jwt.JWTClaimsSet;
import com.sigpwned.dropwizard.jose.jwt.example.webapp.model.Account;

public final class Accounts {
  private Accounts() {}

  public static final Account ADMINISTRATOR = Account.of("100", "administrator", "Big Boss");

  public static final Account USER = Account.of("200", "user", "Some Guy");

  /**
   * Convers the given {@link Account} to {@link JWTClaimsSet}.
   */
  public static JWTClaimsSet toClaims(Account a) {
    return new JWTClaimsSet.Builder().claim(Claims.ACCOUNT_ID_CLAIM, a.getId())
        .claim(Claims.ACCOUNT_USERNAME_CLAIM, a.getUsername())
        .claim(Claims.ACCOUNT_NAME_CLAIM, a.getName()).build();
  }

  /**
   * Converts the given {@link JWTClaimsSet} into an {@link Account}.
   * 
   * @throws IllegalArgumentException if the claims cannot be mapped to an account
   */
  public static Account fromClaims(JWTClaimsSet cs) {
    String id = getRequiredStringClaim(cs, Claims.ACCOUNT_ID_CLAIM);
    String username = getRequiredStringClaim(cs, Claims.ACCOUNT_USERNAME_CLAIM);
    String name = getRequiredStringClaim(cs, Claims.ACCOUNT_NAME_CLAIM);
    return Account.of(id, username, name);
  }

  /**
   * Retrieves the {@link String} claim with the given name from the given claims set
   * 
   * @throws IllegalArgumentException if the claim is not present or cannot be represented as a
   *         string
   */
  private static String getRequiredStringClaim(JWTClaimsSet cs, String name) {
    try {
      return Optional.ofNullable(cs.getStringClaim(name))
          .orElseThrow(() -> new IllegalArgumentException("Missing required claim " + name));
    } catch (ParseException e) {
      throw new IllegalArgumentException("Failed to parse required claim " + name, e);
    }
  }
}
