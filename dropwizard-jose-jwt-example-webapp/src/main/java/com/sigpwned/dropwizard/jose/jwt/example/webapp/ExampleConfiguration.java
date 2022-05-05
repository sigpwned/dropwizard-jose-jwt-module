package com.sigpwned.dropwizard.jose.jwt.example.webapp;

import javax.validation.Valid;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sigpwned.dropwizard.jose.jwt.JWTBundleConfiguration;
import com.sigpwned.dropwizard.jose.jwt.JWTConfiguration;
import com.sigpwned.dropwizard.jose.jwt.example.webapp.configuration.AccountStoreConfiguration;
import io.dropwizard.core.Configuration;

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
