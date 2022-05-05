package com.sigpwned.dropwizard.jose.jwt.example.webapp.auth;

import javax.ws.rs.container.ContainerRequestContext;
import org.checkerframework.checker.nullness.qual.Nullable;
import com.sigpwned.dropwizard.jose.jwt.example.webapp.model.Account;
import io.dropwizard.auth.Authorizer;

/**
 * We use a trivially simple authorization model. A real application might include more information
 * on the account and make authorization decisions on that basis.
 */
public class ExampleAuthorizer implements Authorizer<Account> {
  @Override
  public boolean authorize(Account principal, String role,
      @Nullable ContainerRequestContext requestContext) {
    // We have no roles. Everyone can do everything.
    return true;
  }
}
