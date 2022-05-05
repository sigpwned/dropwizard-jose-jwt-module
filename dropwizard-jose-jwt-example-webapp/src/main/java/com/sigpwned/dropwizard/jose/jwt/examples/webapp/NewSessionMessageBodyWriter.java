package com.sigpwned.dropwizard.jose.jwt.examples.webapp;

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
import io.dropwizard.jackson.Jackson;

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
    // It's safe to build the header value this way because both the Cookie key and value are both
    // URL safe. The string "token" is URL safe by inspection, and JWTs are URL safe by design.
    httpHeaders.add(HttpHeaders.SET_COOKIE,
        String.format("%s=%s", "token", t.getToken().serialize()));

    // Write the value like normal.
    mapper.writeValue(entityStream, t.getAccount());
  }
}
