package com.sigpwned.dropwizard.jose.jwt.examples.webapp;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.sigpwned.dropwizard.jose.jwt.JWTBundle;
import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;

public class ExampleWebapp extends Application<ExampleConfiguration> {
  public static void main(String[] args) throws Exception {
    new ExampleWebapp().run(args);
  }

  @Override
  public String getName() {
    return "ExampleWebapp";
  }

  @Override
  public void initialize(Bootstrap<ExampleConfiguration> bootstrap) {
    bootstrap.getObjectMapper().disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    bootstrap.addBundle(new JWTBundle<>(new ExampleAuthenticator(), new ExampleAuthorizer()));
  }

  @Override
  public void run(ExampleConfiguration configuration, Environment environment) throws Exception {
    environment.jersey().register(NewSessionMessageBodyWriter.class);

    environment.jersey().register(MeResource.class);
    environment.jersey().register(LoginResource.class);
  }
}
