package com.sigpwned.dropwizard.jose.jwt.examples.webapp;

import static java.util.stream.Collectors.toUnmodifiableMap;
import java.io.IOException;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import com.nimbusds.jwt.SignedJWT;
import com.sigpwned.dropwizard.jose.jwt.JWTFactory;

@Path("/login")
public class LoginResource {

  private static final Map<String, Account> ACCOUNTS =
      Stream.of(Accounts.ADMINISTRATOR, Accounts.USER)
          .collect(toUnmodifiableMap(Account::getUsername, Function.identity()));

  private static final String PASSWORD = "password";

  private final JWTFactory tokenFactory;

  @Inject
  public LoginResource(JWTFactory tokenFactory) {
    this.tokenFactory = tokenFactory;
  }

  @POST
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(MediaType.APPLICATION_JSON)
  public NewSession login(@FormParam("username") String username,
      @FormParam("password") String password) throws IOException {
    // Obviously, in a real implementation, you would do something not insane here.
    Account account = ACCOUNTS.get(username);
    if (account == null)
      throw new NotFoundException();

    // And you'd like to use a real challenge here...
    if (!password.equals(PASSWORD))
      throw new NotAuthorizedException("password");

    SignedJWT token = getTokenFactory().create(account.toClaims());

    return NewSession.of(token, account);
  }

  /**
   * @return the accounts
   */
  public static Map<String, Account> getAccounts() {
    return ACCOUNTS;
  }

  /**
   * @return the password
   */
  public static String getPassword() {
    return PASSWORD;
  }

  /**
   * @return the tokenFactory
   */
  public JWTFactory getTokenFactory() {
    return tokenFactory;
  }
}
