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
package com.sigpwned.dropwizard.jose.jwt.example.webapp.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nimbusds.jwt.SignedJWT;
import com.sigpwned.dropwizard.jose.jwt.example.webapp.linting.Generated;

public class NewSession {
  @JsonCreator
  public static NewSession of(@JsonProperty("token") SignedJWT token,
      @JsonProperty("account") Account account) {
    return new NewSession(token, account);
  }

  private final SignedJWT token;
  private final Account account;

  @Generated
  public NewSession(SignedJWT token, Account account) {
    this.token = token;
    this.account = account;
  }

  /**
   * @return the token
   */
  @Generated
  public SignedJWT getToken() {
    return token;
  }

  /**
   * @return the account
   */
  @Generated
  public Account getAccount() {
    return account;
  }

  @Override
  @Generated
  public int hashCode() {
    return Objects.hash(account, token);
  }

  @Override
  @Generated
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    NewSession other = (NewSession) obj;
    return Objects.equals(account, other.account)
        && Objects.equals(token.serialize(), other.token.serialize());
  }

  @Override
  @Generated
  public String toString() {
    return "NewSession [token=" + token + ", account=" + account + "]";
  }
}
