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
package com.sigpwned.dropwizard.jose.jwt.example.webapp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sigpwned.dropwizard.jose.jwt.JWTBundleConfiguration;
import com.sigpwned.dropwizard.jose.jwt.JWTConfiguration;
import com.sigpwned.dropwizard.jose.jwt.example.webapp.configuration.AccountStoreConfiguration;
import io.dropwizard.core.Configuration;
import jakarta.validation.Valid;

public class ExampleConfiguration extends Configuration implements JWTBundleConfiguration {
  @Valid
  private JWTConfiguration jwts;

  @Valid
  private AccountStoreConfiguration login;

  /**
   * @return the jwts
   */
  public JWTConfiguration getJwts() {
    return jwts;
  }

  /**
   * @param jwts the jwts to set
   */
  public void setJwts(JWTConfiguration jwts) {
    this.jwts = jwts;
  }

  /**
   * @return the accounts
   */
  public AccountStoreConfiguration getLogin() {
    return login;
  }

  /**
   * @param login the accounts to set
   */
  public void setAccounts(AccountStoreConfiguration login) {
    this.login = login;
  }

  @Override
  @JsonIgnore
  public JWTConfiguration getJWTConfiguration() {
    return getJwts();
  }
}
