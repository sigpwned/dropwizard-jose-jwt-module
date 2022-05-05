package com.sigpwned.dropwizard.jose.jwt.examples.webapp;

import javax.ws.rs.container.ContainerRequestContext;
import org.checkerframework.checker.nullness.qual.Nullable;
import io.dropwizard.auth.Authorizer;

public class ExampleAuthorizer implements Authorizer<Account> {
  @Override
  public boolean authorize(Account principal, String role,
      @Nullable ContainerRequestContext requestContext) {
    return true;
  }
}
