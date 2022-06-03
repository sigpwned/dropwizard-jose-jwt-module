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

import com.sigpwned.dropwizard.jose.jwt.example.webapp.linting.VisibleForTesting;
import com.sigpwned.dropwizard.jose.jwt.example.webapp.model.Account;
import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;

/**
 * A simple example endpoint that returns the current user. Note that the class is annotated with
 * {@link PermitAll}, which requires all calls to this endpoint to include credentials.
 */
@PermitAll
@Path("/me")
public class MeResource {
  @Context
  @VisibleForTesting
  SecurityContext context;

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Account getMe() {
    return (Account) context.getUserPrincipal();
  }
}
