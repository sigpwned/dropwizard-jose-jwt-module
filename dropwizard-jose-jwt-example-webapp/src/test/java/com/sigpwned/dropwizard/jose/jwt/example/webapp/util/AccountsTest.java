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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;
import com.nimbusds.jwt.JWTClaimsSet;
import com.sigpwned.dropwizard.jose.jwt.example.webapp.model.Account;

public class AccountsTest {
  @Test
  public void shouldConvertAccountToClaimsProperly() {
    final String id = "id";
    final String username = "username";
    final String name = "name";

    JWTClaimsSet claims = Accounts.toClaims(Account.of(id, username, name));

    assertThat(claims,
        is(new JWTClaimsSet.Builder().claim(Claims.ACCOUNT_ID_CLAIM, id)
            .claim(Claims.ACCOUNT_USERNAME_CLAIM, username).claim(Claims.ACCOUNT_NAME_CLAIM, name)
            .build()));
  }

  @Test
  public void shouldConvertClaimsToAccountProperly() {
    final String id = "id";
    final String username = "username";
    final String name = "name";

    Account account = Accounts.fromClaims(new JWTClaimsSet.Builder()
        .claim(Claims.ACCOUNT_ID_CLAIM, id).claim(Claims.ACCOUNT_USERNAME_CLAIM, username)
        .claim(Claims.ACCOUNT_NAME_CLAIM, name).build());

    assertThat(account, is(Account.of(id, username, name)));
  }
}
