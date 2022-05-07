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
package com.sigpwned.dropwizard.jose.jwt.example.webapp.configuration;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sigpwned.dropwizard.jose.jwt.example.webapp.AccountStore;
import com.sigpwned.dropwizard.jose.jwt.example.webapp.linting.Generated;
import com.sigpwned.dropwizard.jose.jwt.example.webapp.model.Account;

/**
 * Obviously, this is a terrible, horrible, no good, very bad account store. In the real world, you
 * would do something at least slightly sane, like use a database or a proper authentication
 * backend. But in this little example world we're inhabiting for the moment, this will do just
 * fine.
 */
public class AccountStoreConfiguration {
  public static class AccountCredentials extends Account {
    @JsonCreator
    public static AccountCredentials of(@JsonProperty("id") String id,
        @JsonProperty("username") String username, @JsonProperty("name") String name,
        @JsonProperty("password") String password) {
      return new AccountCredentials(id, username, name, password);
    }

    @NotEmpty
    private final String password;

    @Generated
    public AccountCredentials(String id, String username, String name, String password) {
      super(id, username, name);
      this.password = password;
    }

    /**
     * @return the password
     */
    public String getPassword() {
      return password;
    }

    @Override
    @Generated
    public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + Objects.hash(password);
      return result;
    }

    @Override
    @Generated
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (!super.equals(obj))
        return false;
      if (getClass() != obj.getClass())
        return false;
      AccountCredentials other = (AccountCredentials) obj;
      return Objects.equals(password, other.password);
    }

    @Override
    @Generated
    public String toString() {
      return "AccountCredentials [getPassword()=" + "*".repeat(getPassword().length())
          + ", getId()=" + getId() + ", getUsername()=" + getUsername() + ", getName()=" + getName()
          + "]";
    }
  }

  @Valid
  @NotEmpty
  private List<AccountCredentials> accounts;

  /**
   * @return the accounts
   */
  @Generated
  public List<AccountCredentials> getAccounts() {
    return accounts;
  }

  /**
   * @param accounts the accounts to set
   */
  @Generated
  public void setAccounts(List<AccountCredentials> accounts) {
    this.accounts = accounts;
  }

  public AccountStore buildAccountStore() {
    return new AccountStore() {
      @Override
      public Optional<Account> authenticate(String username, String password) throws IOException {
        return getAccounts().stream()
            .filter(a -> Objects.equals(a.getUsername(), username)
                && Objects.equals(a.getPassword(), password))
            .map(ac -> Account.of(ac.getId(), ac.getUsername(), ac.getName())).findFirst();
      }
    };
  }
}
