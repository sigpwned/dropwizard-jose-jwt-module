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

import com.sigpwned.dropwizard.jose.jwt.example.webapp.model.Account;

public final class Claims {
  private Claims() {}

  /**
   * The name of the account ID JWT claim
   * 
   * @see Account#getId()
   */
  public static final String ACCOUNT_ID_CLAIM = "accountId";

  /**
   * The name of the account username JWT claim
   * 
   * @see Account#getUsername()
   */
  public static final String ACCOUNT_USERNAME_CLAIM = "accountUsername";

  /**
   * The name of the account name JWT claim
   * 
   * @see Account#getName()
   */
  public static final String ACCOUNT_NAME_CLAIM = "accountName";
}
