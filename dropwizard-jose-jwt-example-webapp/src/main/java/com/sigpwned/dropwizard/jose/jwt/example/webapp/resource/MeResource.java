package com.sigpwned.dropwizard.jose.jwt.example.webapp.resource;

import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import com.sigpwned.dropwizard.jose.jwt.example.webapp.model.Account;

/**
 * A simple example endpoint that returns the current user. Note that the class is annotated with
 * {@link PermitAll}, which requires all calls to this endpoint to include credentials.
 */
@PermitAll
@Path("/me")
public class MeResource {
  @Context
  private SecurityContext context;

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Account getMe() {
    return (Account) context.getUserPrincipal();
  }
}
