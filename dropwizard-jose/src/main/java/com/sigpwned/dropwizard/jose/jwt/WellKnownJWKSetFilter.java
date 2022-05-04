/*-
 * =================================LICENSE_START==================================
 * dropwizard-jwt
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
package com.sigpwned.dropwizard.jose.jwt;

import java.io.IOException;
import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import com.nimbusds.jose.jwk.JWKSet;

public class WellKnownJWKSetFilter implements ContainerRequestFilter {
  private final JWKSet jwks;

  @Inject
  public WellKnownJWKSetFilter(JWKSet jwks) {
    // Make sure we only expose public data
    this.jwks = jwks.toPublicJWKSet();
  }

  public JWKSet getJwks() {
    return jwks;
  }

  @Override
  public void filter(ContainerRequestContext requestContext) throws IOException {
    if (requestContext.getUriInfo().getAbsolutePath().getPath().equals("/.well-known/jwks.json")) {
      requestContext.abortWith(Response.ok().entity(getJwks()).build());
    }
  }
}
