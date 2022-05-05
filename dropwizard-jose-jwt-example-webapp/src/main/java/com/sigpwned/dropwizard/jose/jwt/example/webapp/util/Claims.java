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
