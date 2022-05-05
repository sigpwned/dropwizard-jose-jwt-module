package com.sigpwned.dropwizard.jose.jwt.examples.webapp;

import java.util.Objects;
import com.nimbusds.jwt.SignedJWT;

public class NewSession {
  public static NewSession of(SignedJWT token, Account account) {
    return new NewSession(token, account);
  }

  private final SignedJWT token;
  private final Account account;

  public NewSession(SignedJWT token, Account account) {
    this.token = token;
    this.account = account;
  }

  /**
   * @return the token
   */
  public SignedJWT getToken() {
    return token;
  }

  /**
   * @return the account
   */
  public Account getAccount() {
    return account;
  }

  @Override
  public int hashCode() {
    return Objects.hash(account, token);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    NewSession other = (NewSession) obj;
    return Objects.equals(account, other.account) && Objects.equals(token, other.token);
  }

  @Override
  public String toString() {
    return "NewSession [token=" + token + ", account=" + account + "]";
  }
}
