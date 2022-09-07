# DROPWIZARD JOSE JWT MODULE [![tests](https://github.com/sigpwned/dropwizard-jose-jwt-module/actions/workflows/tests.yml/badge.svg)](https://github.com/sigpwned/dropwizard-jose-jwt-module/actions/workflows/tests.yml) [![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=sigpwned_dropwizard-jose-jwt-module&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=sigpwned_dropwizard-jose-jwt-module) [![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=sigpwned_dropwizard-jose-jwt-module&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=sigpwned_dropwizard-jose-jwt-module) [![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=sigpwned_dropwizard-jose-jwt-module&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=sigpwned_dropwizard-jose-jwt-module) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.sigpwned/dropwizard-jose-jwt-module/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.sigpwned/dropwizard-jose-jwt-module)

dropwizard-jose-jwt-module adds [JOSE](https://www.redhat.com/en/blog/jose-json-object-signing-and-encryption)-compliant stateless [JWTs](https://en.wikipedia.org/wiki/JSON_Web_Token) to [Dropwizard](https://www.dropwizard.io/) 2.0.x, 2.1.x, 3.0.x, and 4.0.x.

## Goals

* Provide JOSE-compliant stateless JWTs to Dropwizard

## Non-Goals

* Support other web frameworks
* Support stateful sessions
* Support other parts of the JOSE standard

## What is JOSE?

JOSE is the [JavaScript Object Signing and Encryption](https://datatracker.ietf.org/group/jose/documents/) group of industry standards. Broadly, it covers how session and authentication data is represented, encrypted, and signed in JSON.

## What is a JWT?

JWTs are [JavaScript Web Tokens](https://jwt.io/). JSON Web Tokens are an open, industry standard [RFC 7519](https://tools.ietf.org/html/rfc7519) method for representing claims securely between two parties. JWTs are typically used to create "stateless" sessions, where all session data is stored in a JWT which is used as a session ID, as opposed using a surrogate session ID and a persistent store on a server. In this mode, JWTs are typically signed using [a public key cryptosystem](https://en.wikipedia.org/wiki/Public-key_cryptography) (e.g., [RSA](https://en.wikipedia.org/wiki/RSA_%28cryptosystem%29), which allows both the client and server to verify and therefore trust the contents of the token.

JWTs look like this:

    eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c

For more information about JWTs, visit the excellent [jwt.io](https://jwt.io/).

## How do I add stateless JWTs to my Dropwizard webapp?

To add stateless JWTs to a Dropwizard webapp, simply add the `JWTBundle` to your application in the `initialize()` method of your application:

    public class ExampleWebapp extends Application<ExampleConfiguration> {
      public static void main(String[] args) throws Exception {
        new ExampleWebapp().run(args);
      }
    
      @Override
      public void initialize(Bootstrap<ExampleConfiguration> bootstrap) {
        // ADD THIS METHOD TO YOUR WEBAPP
        bootstrap.addBundle(new JWTBundle<>(new ExampleAuthenticator(), new ExampleAuthorizer()));
      }

      // The rest of your webapp
    }

You will need to "bring your own" [authenticator and authorizer](https://www.dropwizard.io/en/latest/manual/auth.html).

### How do I build my authenticator?

In the context of stateless JWTs, the authenticator is essentially a mapping between the JWT claims and your application's user `Principal` type. You can find the example webapp's authenticator [here](https://github.com/sigpwned/dropwizard-jose-jwt-module/blob/main/dropwizard-jose-jwt-example-webapp/src/main/java/com/sigpwned/dropwizard/jose/jwt/example/webapp/auth/ExampleAuthenticator.java).

### How do I build my authorizer?

In Dropwizard, the authorizer controls whether a given `Principal` is allowed to take a specific action. If your application doesn't have roles and all users are created equal, then the authorizer can be just a function that returns true. You can find the example webapp's authorizer [here](https://github.com/sigpwned/dropwizard-jose-jwt-module/blob/main/dropwizard-jose-jwt-example-webapp/src/main/java/com/sigpwned/dropwizard/jose/jwt/example/webapp/auth/ExampleAuthorizer.java).

## What does the bundle add to my webapp?

The bundle adds the following features to your webapp "out of the box":

* It adds the standard `GET /.well-known/jwks.json` endpoint that shares your application's public keys so clients can verify JWTs. Note that in order for this endpoint to work, your Dropwizard `applicationContextPath` must be `/`, but your `rootPath` can be anything.
* Using your authenticator and authorizer, it adds a filter that accepts JWTs as credentials from the following inputs:
  * A query parameter called `token`
  * A cookie parameter called `token`
  * A bearer token on the Authorization header
* Registers a `JWTFactory` for dependency injection for minting new JWTs

The bundle does not add features to issue new JWTs.

## How do I issue new JWTs?

Your application should include a login flow that takes traditional credentials (e.g., SSO, a username/password, etc.) and returns a fresh JWT as a session identifier, perhaps as a cookie. The [`LoginResource`](https://github.com/sigpwned/dropwizard-jose-jwt-module/blob/main/dropwizard-jose-jwt-example-webapp/src/main/java/com/sigpwned/dropwizard/jose/jwt/example/webapp/resource/LoginResource.java) in the example webapp is a good example.

## What keys should I use?

Stateless JWTs are signed using a public key cryptosystem. By default, this bundle uses `RSA256`, which is an RSA signature encoded using [SHA256](https://en.wikipedia.org/wiki/SHA-2). You can find a tool for generating keys in [the dropwizard-jose-jwt-keygen-tool module](https://github.com/sigpwned/dropwizard-jose-jwt-module/tree/main/dropwizard-jose-jwt-keygen-tool).

## Where can I find an example?

You can find a [SSCCE](http://sscce.org/) Dropwizard webapp in this repository in [the dropwizard-jose-jwt-example-webapp module](https://github.com/sigpwned/dropwizard-jose-jwt-module/tree/main/dropwizard-jose-jwt-example-webapp).

For simplicity, this example includes its private keys in the repository. As a rule, you should never check secrets into your source code repository.
