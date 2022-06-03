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
package com.sigpwned.dropwizard.jose.jwt.example.webapp.resource;

import java.io.IOException;
import com.nimbusds.jwt.SignedJWT;
import com.sigpwned.dropwizard.jose.jwt.JWTFactory;
import com.sigpwned.dropwizard.jose.jwt.example.webapp.AccountStore;
import com.sigpwned.dropwizard.jose.jwt.example.webapp.linting.Generated;
import com.sigpwned.dropwizard.jose.jwt.example.webapp.model.Account;
import com.sigpwned.dropwizard.jose.jwt.example.webapp.model.NewSession;
import com.sigpwned.dropwizard.jose.jwt.example.webapp.util.Accounts;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * A simple example endpoint that simulates a user logging in. Note that the class has no
 * requirement that the user be authenticated to call this endpoint, since this is how the user gets
 * authenticated in the first place!
 */
@Path("/login")
public class LoginResource {
  private final AccountStore accountStore;
  private final JWTFactory tokenFactory;

  @Inject
  @Generated
  public LoginResource(AccountStore accountStore, JWTFactory tokenFactory) {
    this.accountStore = accountStore;
    this.tokenFactory = tokenFactory;
  }

  @POST
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(MediaType.APPLICATION_JSON)
  public NewSession login(@FormParam("username") String username,
      @FormParam("password") String password) throws IOException {
    Account account = getAccountStore().authenticate(username, password)
        .orElseThrow(() -> new NotAuthorizedException("password"));

    SignedJWT token = getTokenFactory().create(Accounts.toClaims(account));

    return NewSession.of(token, account);
  }

  /**
   * @return the accountStore
   */
  @Generated
  public AccountStore getAccountStore() {
    return accountStore;
  }

  /**
   * @return the tokenFactory
   */
  @Generated
  public JWTFactory getTokenFactory() {
    return tokenFactory;
  }
}
