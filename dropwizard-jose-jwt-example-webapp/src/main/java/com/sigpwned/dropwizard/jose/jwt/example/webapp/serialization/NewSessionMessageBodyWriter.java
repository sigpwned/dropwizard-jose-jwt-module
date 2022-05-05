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

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.HttpHeaders;
import com.sigpwned.dropwizard.jose.jwt.example.webapp.model.NewSession;
import io.dropwizard.jackson.Jackson;

/**
 * We use this writer to set the session cookie in the user's browser.
 */
@Provider
@Produces(MediaType.APPLICATION_JSON)
public class NewSessionMessageBodyWriter implements MessageBodyWriter<NewSession> {
  private final ObjectMapper mapper;

  public NewSessionMessageBodyWriter() {
    this.mapper = Jackson.newObjectMapper();
  }

  @Override
  public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations,
      MediaType mediaType) {
    return type.equals(NewSession.class);
  }

  @Override
  public void writeTo(NewSession t, Class<?> type, Type genericType, Annotation[] annotations,
      MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
      throws IOException, WebApplicationException {
    // This is where we set our session ID JWT as a cookie for the user's browser.
    // It's safe to build the header value this way because both the Cookie key and value are both
    // URL safe. The string "token" is URL safe by inspection, and JWTs are URL safe by design.
    httpHeaders.add(HttpHeaders.SET_COOKIE,
        String.format("%s=%s", "token", t.getToken().serialize()));

    // Otherwise, we write the account object like normal.
    mapper.writeValue(entityStream, t.getAccount());
  }
}
