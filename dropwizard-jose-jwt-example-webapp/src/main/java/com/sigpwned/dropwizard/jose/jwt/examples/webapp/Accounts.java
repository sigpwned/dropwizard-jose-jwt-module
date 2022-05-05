package com.sigpwned.dropwizard.jose.jwt.examples.webapp;

public final class Accounts {
  private Accounts() {}

  public static final Account ADMINISTRATOR = Account.of("100", "administrator", "Big Boss");

  public static final Account USER = Account.of("200", "user", "Some Guy");
}
