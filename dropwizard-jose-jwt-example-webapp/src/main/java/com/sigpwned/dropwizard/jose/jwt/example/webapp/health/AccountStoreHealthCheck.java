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
