package com.sigpwned.dropwizard.jose.jwt.example.webapp.resource;

import java.io.IOException;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import com.nimbusds.jwt.SignedJWT;
import com.sigpwned.dropwizard.jose.jwt.JWTFactory;
import com.sigpwned.dropwizard.jose.jwt.example.webapp.AccountStore;
import com.sigpwned.dropwizard.jose.jwt.example.webapp.model.Account;
import com.sigpwned.dropwizard.jose.jwt.example.webapp.model.NewSession;
import com.sigpwned.dropwizard.jose.jwt.example.webapp.util.Accounts;

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
  public AccountStore getAccountStore() {
    return accountStore;
  }

  /**
   * @return the tokenFactory
   */
  public JWTFactory getTokenFactory() {
    return tokenFactory;
  }
}
