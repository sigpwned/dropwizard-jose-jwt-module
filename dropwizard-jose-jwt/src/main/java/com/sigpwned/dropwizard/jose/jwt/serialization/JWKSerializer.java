package com.sigpwned.dropwizard.jose.jwt.serialization;

import java.io.IOException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.nimbusds.jose.jwk.JWK;

public class JWKSerializer extends StdSerializer<JWK> {
  private static final long serialVersionUID = 2947217023285929942L;

  public JWKSerializer() {
    super(JWK.class);
  }

  @Override
  public void serialize(JWK value, JsonGenerator gen, SerializerProvider provider)
      throws IOException {
    gen.writeObject(value.toJSONObject());
  }
}
