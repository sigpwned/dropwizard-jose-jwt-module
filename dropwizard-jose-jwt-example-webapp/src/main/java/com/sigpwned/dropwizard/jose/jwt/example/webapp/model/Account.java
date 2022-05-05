package com.sigpwned.dropwizard.jose.jwt.example.webapp.model;

import java.security.Principal;
import java.util.Objects;
import javax.validation.constraints.NotEmpty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Account implements Principal {
  @JsonCreator
  public static Account of(@JsonProperty("id") String id, @JsonProperty("username") String username,
      @JsonProperty("name") String name) {
    return new Account(id, username, name);
  }

  @NotEmpty
  private final String id;

  @NotEmpty
  private final String username;

  @NotEmpty
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
}
