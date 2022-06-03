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
package com.sigpwned.dropwizard.jose.jwt.example.webapp.serialization;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import org.junit.Test;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.SignedJWT;
import com.sigpwned.dropwizard.jose.jwt.JWTAuthFilter;
import com.sigpwned.dropwizard.jose.jwt.example.webapp.model.Account;
import com.sigpwned.dropwizard.jose.jwt.example.webapp.model.NewSession;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;

public class NewSessionMessageBodyWriterTest {
  public static final String EXAMPLE_SIGNED_JWT_STRING =
      "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";


  @Test
  public void shouldSerializeProperly() throws Exception {
    ObjectMapper mapper = mock(ObjectMapper.class);

    NewSessionMessageBodyWriter unit = new NewSessionMessageBodyWriter() {
      @Override
      protected ObjectMapper newObjectMapper() {
        return mapper;
      }
    };

    SignedJWT jwt = SignedJWT.parse(EXAMPLE_SIGNED_JWT_STRING);

    NewSession ns = NewSession.of(jwt, Account.of("id", "username", "name"));

    OutputStream entityStream = mock(OutputStream.class);

    MultivaluedMap<String, Object> httpHeaders = new MultivaluedHashMap<>();

    unit.writeTo(ns, NewSession.class, NewSession.class, new Annotation[0],
        MediaType.APPLICATION_JSON_TYPE, httpHeaders, entityStream);

    assertThat(httpHeaders.getFirst(HttpHeaders.SET_COOKIE),
        is(JWTAuthFilter.DEFAULT_COOKIE_PARAMETER_NAME + "=" + jwt.serialize()));

    verify(mapper).writeValue(entityStream, ns.getAccount());
  }
}
