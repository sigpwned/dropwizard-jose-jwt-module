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
import java.nio.charset.StandardCharsets;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.NotAcceptableException;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MediaType;
import com.nimbusds.jose.jwk.JWKSet;

/**
 * Note that this is an {@link HttpFilter} and not a {@link ContainerRequestFilter}. Because the
 * standard requires the keys to be published at the domain root and web applications can have a
 * prefix (e.g., /v1) that prevents publishing resources at the domain root, it is necessary to use
 * a servlet filter as opposed to a JAX-RS filter.
 */
public class WellKnownJWKSetHttpFilter extends HttpFilter {
  private static final long serialVersionUID = -765773712787780302L;

  public static final String WELL_KNOWN_JWKS_JSON_PATH = "/.well-known/jwks.json";

  private final JWKSet jwks;

  public WellKnownJWKSetHttpFilter(JWKSet jwks) {
    // Make sure we only expose public data
    this.jwks = jwks.toPublicJWKSet();
  }

  public JWKSet getJwks() {
    return jwks;
  }

  @Override
  protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
      throws IOException, ServletException {
    if (req.getRequestURI().equals(WELL_KNOWN_JWKS_JSON_PATH)) {
      if (!req.getMethod().equalsIgnoreCase("GET"))
        throw new NotAcceptableException();
      // TODO Content negotiation
      res.setContentType(MediaType.APPLICATION_JSON);
      try (ServletOutputStream out = res.getOutputStream()) {
        out.write(getJwks().toString().getBytes(StandardCharsets.UTF_8));
      }
    } else {
      chain.doFilter(req, res);
    }
  }
}
