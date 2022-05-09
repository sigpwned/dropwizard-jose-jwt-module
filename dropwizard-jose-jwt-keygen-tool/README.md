# DROPWIZARD JOSE JWT KEYGEN TOOL

This module contains a tool for generated keys for use in an application using `JWTBundle`. It generates a private RSA key and an X500 certificate chain for the public key and outputs a keystore to STDOUT in PKCS12 format.

## Running the tool

First, clone the repository. Next, run the following command in the repository root directory:

    $ mvn clean compile install

Next, run the tool using:

    $ java -jar dropwizard-jose-jwt-keygen-tool/target/dropwizard-jose-jwt-keygen-tool.jar -r $realm -r $password >/path/to/keystore.p12

## Usage

The tool expects the following parameters:

    $ java -jar dropwizard-jose-jwt-keygen-tool/target/dropwizard-jose-jwt-keygen-tool.jar --help
    Usage: [ flags | options ]
    
    Flags:
    --help                              Print this help message
    --version                           The current version of this software
    
    Options:
    -e, --expirationMonths <integer>    The expiration period of the generated keys in months. The
        default period is 12 months, or 1 year.
    -p, --password <string>             The password used to encrypt the keystore.
    -r, --realm <string>                The authentication realm, which is typically the webapp domain.
        This is used to set the common name (CN) claim of the public key certificate.

## Example

The following command was used to generate the keys for the example webapp:

    $ java -jar dropwizard-jose-jwt-keygen-tool/target/dropwizard-jose-jwt-keygen-tool.jar -e 120 -r example -p 'password' >dropwizard-jose-jwt-example-webapp/keys.p12