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
package com.sigpwned.dropwizard.jose.jwt.example.webapp;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import com.sigpwned.dropwizard.jose.jwt.JWTBundle;
import com.sigpwned.dropwizard.jose.jwt.example.webapp.auth.ExampleAuthenticator;
import com.sigpwned.dropwizard.jose.jwt.example.webapp.auth.ExampleAuthorizer;
import com.sigpwned.dropwizard.jose.jwt.example.webapp.health.AccountStoreHealthCheck;
import com.sigpwned.dropwizard.jose.jwt.example.webapp.resource.LoginResource;
import com.sigpwned.dropwizard.jose.jwt.example.webapp.resource.MeResource;
import com.sigpwned.dropwizard.jose.jwt.example.webapp.serialization.NewSessionMessageBodyWriter;
import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;

/**
 * This web application uses JWTs as a stateless session ID for a user-facing SPA. (If we use our
 * imaginations.)
 */
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
    // We register the JWT bundle here in the initialization function. This means that any arguments
    // to the bundle -- i.e., authenticator and authorizer -- must be created without access to data
    // created from the configuration. This is a good fit for JWTs, which are designed to make authn
    // and authz stateless. However, if your application's authn or authz treats JWTs statefully --
    // for example, by using them as a session ID for a persistent session store -- then you may
    // need to initialize your application directly as opposed to by using the bundle. It should be
    // fairly easy to work out how to do that by reading the JWTBundle code.
    bootstrap.addBundle(new JWTBundle<>(new ExampleAuthenticator(), new ExampleAuthorizer()));
  }

  @Override
  public void run(ExampleConfiguration configuration, Environment environment) throws Exception {
    // Let's set up our account store. This will be used to log users in with traditional username
    // and password credentials in the /login endpoint.
    AccountStore accountStore = configuration.getLogin().buildAccountStore();

    environment.jersey().register(new AbstractBinder() {
      @Override
      protected void configure() {
        bind(accountStore).to(AccountStore.class);
      }
    });

    // Our application returns a NewSession when the user logs in using the /login endpoint. We
    // register a custom MessageBodyWriter for that type so that we can set a cookie containing the
    // JWT session ID for the user's browser.
    environment.jersey().register(NewSessionMessageBodyWriter.class);

    // Our application's resources are simple: you can login, and ask who you are.
    environment.jersey().register(MeResource.class);
    environment.jersey().register(LoginResource.class);

    // Make sure our account store is healthy
    environment.healthChecks().register(AccountStoreHealthCheck.NAME,
        new AccountStoreHealthCheck(accountStore));
  }
}
