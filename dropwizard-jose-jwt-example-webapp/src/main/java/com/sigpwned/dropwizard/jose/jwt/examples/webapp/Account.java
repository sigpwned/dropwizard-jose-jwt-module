package com.sigpwned.dropwizard.jose.jwt.examples.webapp;

import java.security.Principal;
import java.util.Objects;
import com.nimbusds.jwt.JWTClaimsSet;

public class Account implements Principal {
  public static Account of(String id, String username, String name) {
    return new Account(id, username, name);
  }

  private final String id;
  private final String username;
  private final String name;

  public Account(String id, String username, String name) {
    this.id = id;
    this.username = username;
    this.name = name;
  }

  /**
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * @return the username
   */
  public String getUsername() {
    return username;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, username);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Account other = (Account) obj;
    return Objects.equals(id, other.id) && Objects.equals(name, other.name)
        && Objects.equals(username, other.username);
  }

  @Override
  public String toString() {
    return "Account [id=" + id + ", username=" + username + ", name=" + name + "]";
  }

  public JWTClaimsSet toClaims() {
    return new JWTClaimsSet.Builder().claim("accountId", getId())
        .claim("accountUsername", getUsername()).claim("accountName", getName()).build();
  }
}
