package com.sigpwned.dropwizard.jose.jwt.examples.webapp;

import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

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
