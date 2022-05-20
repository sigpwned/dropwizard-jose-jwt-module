/*-
 * =================================LICENSE_START==================================
 * dropwizard-jose-jwt
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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.servlet.FilterChain;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import org.junit.Test;
import com.nimbusds.jose.jwk.JWKSet;

public class WellKnownJWKSetHttpFilterTest {
  @Test
  public void shouldCallChainDoFilterIfNotWellKnownJwks() throws Exception {
    JWKSet jwks = new JWKSet();

    WellKnownJWKSetHttpFilter unit = new WellKnownJWKSetHttpFilter(jwks);

    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getRequestURI()).thenReturn("/alpha/bravo/charlie");

    HttpServletResponse response = mock(HttpServletResponse.class);

    FilterChain chain = mock(FilterChain.class);

    unit.doFilter(request, response, chain);

    verify(chain).doFilter(request, response);
  }

  @Test
  public void shouldReturnJwksIfWellKnownJwks() throws Exception {
    JWKSet jwks = new JWKSet();

    WellKnownJWKSetHttpFilter unit = new WellKnownJWKSetHttpFilter(jwks);

    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getMethod()).thenReturn("GET");
    when(request.getRequestURI()).thenReturn(WellKnownJWKSetHttpFilter.WELL_KNOWN_JWKS_JSON_PATH);

    HttpServletResponse response = mock(HttpServletResponse.class);

    ByteArrayOutputStream buf = new ByteArrayOutputStream();
    ServletOutputStream out = new ServletOutputStream() {
      @Override
      public boolean isReady() {
        return true;
      }

      @Override
      public void setWriteListener(WriteListener writeListener) {
        // nop
      }

      @Override
      public void write(int b) throws IOException {
        buf.write(b);
      }
    };
    when(response.getOutputStream()).thenReturn(out);

    FilterChain chain = mock(FilterChain.class);

    unit.doFilter(request, response, chain);

    verify(response).setContentType(MediaType.APPLICATION_JSON);

    assertThat(new String(buf.toByteArray(), StandardCharsets.UTF_8), is(jwks.toString()));
  }
}
