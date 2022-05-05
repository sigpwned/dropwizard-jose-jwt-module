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
package com.sigpwned.dropwizard.jose.jwt.example.webapp.health;

import com.codahale.metrics.health.HealthCheck;
import com.sigpwned.dropwizard.jose.jwt.example.webapp.AccountStore;

/**
 * You should always make sure your external dependencies are healthy.
 */
public class AccountStoreHealthCheck extends HealthCheck {
  public static final String NAME = "AccountStore";

  private final AccountStore accountStore;

  public AccountStoreHealthCheck(AccountStore accountStore) {
    this.accountStore = accountStore;
  }

  @Override
  protected Result check() throws Exception {
    try {
      // We don't care if the credentials work, only that we don't get an exception.
      getAccountStore().authenticate("alpha", "bravo");
      return Result.healthy();
    } catch (Exception e) {
      return Result.unhealthy(e);
    }
  }

  /**
   * @return the accountStore
   */
  public AccountStore getAccountStore() {
    return accountStore;
  }
}
