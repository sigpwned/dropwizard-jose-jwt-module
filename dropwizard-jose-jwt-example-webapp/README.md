# DROPWIZARD JOSE JWT EXAMPLE WEBAPP

This module contains an example webapp that uses JWTs as stateless session identifiers using this repository's `JWTBundle`. The following examples demonstrate the features provided by the bundle and framework code in the example.

## Running the webapp

First, clone the repository. Next, run the following command in the repository root directory:

    $ mvn clean compile install

Next, change to the `dropwizard-jose-jwt-example-webapp` directory:

    $ cd dropwizard-jose-jwt-example-webapp

Finally, run the webapp using:

    $ java -jar target/dropwizard-jose-jwt-example-webapp.jar server config.yml

## Example webapp calls

The example webapp exposes two API endpoints, `/v1/login` and `/v1/me`, and one standards endpoint, `/.well-known/jwks.json`. The below examples show requests run against the example webapp. Some headers have been removed and response bodies have been pretty printed for clarity.

### Get the webapp's well-known JWKs

To fetch the application's well-known JWKs for verifying JWTs, use:

    $ curl -D - -XGET localhost:8080/.well-known/jwks.json
    HTTP/1.1 200 OK
    Content-Type: application/json

    {
      "keys": [
        {
          "kty": "RSA",
          "x5t#S256": "3oZe7rz0CxAIbL59f3MYdgj4R3KSQso__MOQ7ubrcfU",
          "e": "AQAB",
          "kid": "2022-05-05",
          "x5c": [
            "MIICojCCAYqgAwIBAgIGAYCSBAYwMA0GCSqGSIb3DQEBCwUAMBIxEDAOBgNVBAMMB2V4YW1wbGUwHhcNMjIwNTA1MDIxOTM4WhcNMjMwNDMwMDIxOTM4WjASMRAwDgYDVQQDDAdleGFtcGxlMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmvGYEgpvZXNzJxN4DQIDu20kdil0rwZobG0uh4oKjnM1poQwzeDIuDFpWWzlXzExjQfZiUD4tO4N1g/fhGlIZTNbDZNF5SnWkEd4FtOIxL6D1VmYM663s5ThXhz5Ytz0FoRhLog4eVNO8hnIoYqLk6eHlhdSBZ8FHlMKeuOr1w88fNwIO+VbP4p3oponIBmKacanQ90hVmFibUgMuP+j5xPUaz+UkC9egytKOoSac1wpq05AuYtehN4NYXdCxR7k7XhRRfDFPeOguyGmrDqvnS1taKaVNfoyBOcqNkNkuwtHM1F8DdBhsX7aT4tjf5gmrd2h2iutrFJPPJ+u3a418wIDAQABMA0GCSqGSIb3DQEBCwUAA4IBAQAwL/Pukd/rjlT5a7eZQdrkT/J4KrDOXesQkHxBzoa4YAY7Zjdhq0s4abZ1BvM0rysbRy7HL/3I64EOUBcW/KbQ2zKpl9s7X3S3k/qI/GJ8cXpdV8KCJwbxpyRcHutI2agM70CKUoHmF+jjOhhJJNE8f6yEQTpAbYMbNj9fglXUeA6awSfcesToQdDxtoHiE6w6BVYpbQO4K75k6QDmGq6C9MNMzSmZFvMPaFH8LkK27lwHuYCyMdbU/w3xg2yCrv4C8RoY0lVT/H/7ESQVCItneWX/D9c6SCqyk8wv/vA1LihDge1NpgGWpiFZmF3JenLkWKbQIBJBMh+tWwGl+GsE"
          ],
          "n": "mvGYEgpvZXNzJxN4DQIDu20kdil0rwZobG0uh4oKjnM1poQwzeDIuDFpWWzlXzExjQfZiUD4tO4N1g_fhGlIZTNbDZNF5SnWkEd4FtOIxL6D1VmYM663s5ThXhz5Ytz0FoRhLog4eVNO8hnIoYqLk6eHlhdSBZ8FHlMKeuOr1w88fNwIO-VbP4p3oponIBmKacanQ90hVmFibUgMuP-j5xPUaz-UkC9egytKOoSac1wpq05AuYtehN4NYXdCxR7k7XhRRfDFPeOguyGmrDqvnS1taKaVNfoyBOcqNkNkuwtHM1F8DdBhsX7aT4tjf5gmrd2h2iutrFJPPJ-u3a418w"
        }
      ]
    }

For more information about JWKs, see [the standard](https://datatracker.ietf.org/doc/html/rfc7517). A few companies have also developed excellent documentation on JWKs, e.g. [auth0](https://auth0.com/docs/secure/tokens/json-web-tokens/json-web-key-sets) and [connect2id](https://connect2id.com/products/nimbus-jose-jwt/examples/jwk-generation).

### Log in

To log into the example webapp, use:

    $ curl -XPOST -D - localhost:8080/v1/login -d username=administrator -d password='password'     
    HTTP/1.1 200 OK
    Date: Sun, 08 May 2022 17:04:16 GMT
    Content-Type: application/json
    Set-Cookie: token=eyJ4NXQjUzI1NiI6IjNvWmU3cnowQ3hBSWJMNTlmM01ZZGdqNFIzS1NRc29fX01PUTd1YnJjZlUiLCJ4NWMiOlsiTUlJQ29qQ0NBWXFnQXdJQkFnSUdBWUNTQkFZd01BMEdDU3FHU0liM0RRRUJDd1VBTUJJeEVEQU9CZ05WQkFNTUIyVjRZVzF3YkdVd0hoY05Nakl3TlRBMU1ESXhPVE00V2hjTk1qTXdORE13TURJeE9UTTRXakFTTVJBd0RnWURWUVFEREFkbGVHRnRjR3hsTUlJQklqQU5CZ2txaGtpRzl3MEJBUUVGQUFPQ0FROEFNSUlCQ2dLQ0FRRUFtdkdZRWdwdlpYTnpKeE40RFFJRHUyMGtkaWwwcndab2JHMHVoNG9Lam5NMXBvUXd6ZURJdURGcFdXemxYekV4alFmWmlVRDR0TzROMWdcL2ZoR2xJWlROYkRaTkY1U25Xa0VkNEZ0T0l4TDZEMVZtWU02NjNzNVRoWGh6NVl0ejBGb1JoTG9nNGVWTk84aG5Jb1lxTGs2ZUhsaGRTQlo4RkhsTUtldU9yMXc4OGZOd0lPK1ZiUDRwM29wb25JQm1LYWNhblE5MGhWbUZpYlVnTXVQK2o1eFBVYXorVWtDOWVneXRLT29TYWMxd3BxMDVBdVl0ZWhONE5ZWGRDeFI3azdYaFJSZkRGUGVPZ3V5R21yRHF2blMxdGFLYVZOZm95Qk9jcU5rTmt1d3RITTFGOERkQmhzWDdhVDR0amY1Z21yZDJoMml1dHJGSlBQSit1M2E0MTh3SURBUUFCTUEwR0NTcUdTSWIzRFFFQkN3VUFBNElCQVFBd0xcL1B1a2RcL3JqbFQ1YTdlWlFkcmtUXC9KNEtyRE9YZXNRa0h4QnpvYTRZQVk3WmpkaHEwczRhYloxQnZNMHJ5c2JSeTdITFwvM0k2NEVPVUJjV1wvS2JRMnpLcGw5czdYM1Mza1wvcUlcL0dKOGNYcGRWOEtDSndieHB5UmNIdXRJMmFnTTcwQ0tVb0htRitqak9oaEpKTkU4ZjZ5RVFUcEFiWU1iTmo5ZmdsWFVlQTZhd1NmY2VzVG9RZER4dG9IaUU2dzZCVllwYlFPNEs3NWs2UURtR3E2QzlNTk16U21aRnZNUGFGSDhMa0syN2x3SHVZQ3lNZGJVXC93M3hnMnlDcnY0QzhSb1kwbFZUXC9IXC83RVNRVkNJdG5lV1hcL0Q5YzZTQ3F5azh3dlwvdkExTGloRGdlMU5wZ0dXcGlGWm1GM0plbkxrV0tiUUlCSkJNaCt0V3dHbCtHc0UiXSwia2lkIjoiMjAyMi0wNS0wNSIsImFsZyI6IlJTMjU2In0.eyJhY2NvdW50SWQiOiIxMDAiLCJhY2NvdW50TmFtZSI6IkJpZyBCb3NzIiwiYWNjb3VudFVzZXJuYW1lIjoiYWRtaW5pc3RyYXRvciIsImlzcyI6ImV4YW1wbGUiLCJleHAiOjE2NTIwMzMwNTYsImlhdCI6MTY1MjAyOTQ1NiwianRpIjoiMjFjZTdiNmItOTY0Ni00MzgyLTkxMjktNDJiZWQzNzM2NThmIn0.VfqznFUGm57-EqZtwHpAbiRgyCZ3VbIZfy7MLf1Jf-FedOdJj5-lVgCseGLJ3nciN5sZx8YVqIOpn14qnoO19rYdMiPLhd1kL5c7WGw6P80BLKBtsaBQn5DetVA7N0GokFjHRr1I73t6O-3w9rrRdkRHVWHawK2ahChncx0uegnep0UoCDvsl3JoKdyKP5SbTsSWm6y7Uu2v2e9FSmso8CazzEEMB0qdsvF5wC37DfwDKDgNml981wADEqmfgwZXtcVWjbZJpqht1SKS7ejt8vK-DwSXPLzFL5H7fVp83_9t_IRgCE1p5atL8lxwJXJpdadneXapmyjIpjl6Vr8DPQ
    Vary: Accept-Encoding
    Content-Length: 57

    {
      "id": "100",
      "username": "administrator",
      "name": "Big Boss"
    }

Logging in with valid credentials results in a response that contains:

* The JWT to use as a session identifier, which appears in a `Set-Cookie` header
* The current user as the response body

The credentials are stored in `config.yml`. Of course, in a proper application, user credentials would be stored in a proper authentication backend or SSO provider.

### Authenticate a request

The example webapp provides three ways to authenticate a web request:

1. The `token` query parameter
2. The `token` cookie
3. A bearer token in the authorization header

The below examples demonstrate all three techniques using the `/v1/me` endpoint, which returns the current user.

#### Query Parameter

The webapp looks for a JWT in the `token` query parameter first:

    curl -XGET 'localhost:8080/v1/me?token=eyJ4NXQjUzI1NiI6IjNvWmU3cnowQ3hBSWJMNTlmM01ZZGdqNFIzS1NRc29fX01PUTd1YnJjZlUiLCJ4NWMiOlsiTUlJQ29qQ0NBWXFnQXdJQkFnSUdBWUNTQkFZd01BMEdDU3FHU0liM0RRRUJDd1VBTUJJeEVEQU9CZ05WQkFNTUIyVjRZVzF3YkdVd0hoY05Nakl3TlRBMU1ESXhPVE00V2hjTk1qTXdORE13TURJeE9UTTRXakFTTVJBd0RnWURWUVFEREFkbGVHRnRjR3hsTUlJQklqQU5CZ2txaGtpRzl3MEJBUUVGQUFPQ0FROEFNSUlCQ2dLQ0FRRUFtdkdZRWdwdlpYTnpKeE40RFFJRHUyMGtkaWwwcndab2JHMHVoNG9Lam5NMXBvUXd6ZURJdURGcFdXemxYekV4alFmWmlVRDR0TzROMWdcL2ZoR2xJWlROYkRaTkY1U25Xa0VkNEZ0T0l4TDZEMVZtWU02NjNzNVRoWGh6NVl0ejBGb1JoTG9nNGVWTk84aG5Jb1lxTGs2ZUhsaGRTQlo4RkhsTUtldU9yMXc4OGZOd0lPK1ZiUDRwM29wb25JQm1LYWNhblE5MGhWbUZpYlVnTXVQK2o1eFBVYXorVWtDOWVneXRLT29TYWMxd3BxMDVBdVl0ZWhONE5ZWGRDeFI3azdYaFJSZkRGUGVPZ3V5R21yRHF2blMxdGFLYVZOZm95Qk9jcU5rTmt1d3RITTFGOERkQmhzWDdhVDR0amY1Z21yZDJoMml1dHJGSlBQSit1M2E0MTh3SURBUUFCTUEwR0NTcUdTSWIzRFFFQkN3VUFBNElCQVFBd0xcL1B1a2RcL3JqbFQ1YTdlWlFkcmtUXC9KNEtyRE9YZXNRa0h4QnpvYTRZQVk3WmpkaHEwczRhYloxQnZNMHJ5c2JSeTdITFwvM0k2NEVPVUJjV1wvS2JRMnpLcGw5czdYM1Mza1wvcUlcL0dKOGNYcGRWOEtDSndieHB5UmNIdXRJMmFnTTcwQ0tVb0htRitqak9oaEpKTkU4ZjZ5RVFUcEFiWU1iTmo5ZmdsWFVlQTZhd1NmY2VzVG9RZER4dG9IaUU2dzZCVllwYlFPNEs3NWs2UURtR3E2QzlNTk16U21aRnZNUGFGSDhMa0syN2x3SHVZQ3lNZGJVXC93M3hnMnlDcnY0QzhSb1kwbFZUXC9IXC83RVNRVkNJdG5lV1hcL0Q5YzZTQ3F5azh3dlwvdkExTGloRGdlMU5wZ0dXcGlGWm1GM0plbkxrV0tiUUlCSkJNaCt0V3dHbCtHc0UiXSwia2lkIjoiMjAyMi0wNS0wNSIsImFsZyI6IlJTMjU2In0.eyJhY2NvdW50SWQiOiIxMDAiLCJhY2NvdW50TmFtZSI6IkJpZyBCb3NzIiwiYWNjb3VudFVzZXJuYW1lIjoiYWRtaW5pc3RyYXRvciIsImlzcyI6ImV4YW1wbGUiLCJleHAiOjE2NTIwMzMwNTYsImlhdCI6MTY1MjAyOTQ1NiwianRpIjoiMjFjZTdiNmItOTY0Ni00MzgyLTkxMjktNDJiZWQzNzM2NThmIn0.VfqznFUGm57-EqZtwHpAbiRgyCZ3VbIZfy7MLf1Jf-FedOdJj5-lVgCseGLJ3nciN5sZx8YVqIOpn14qnoO19rYdMiPLhd1kL5c7WGw6P80BLKBtsaBQn5DetVA7N0GokFjHRr1I73t6O-3w9rrRdkRHVWHawK2ahChncx0uegnep0UoCDvsl3JoKdyKP5SbTsSWm6y7Uu2v2e9FSmso8CazzEEMB0qdsvF5wC37DfwDKDgNml981wADEqmfgwZXtcVWjbZJpqht1SKS7ejt8vK-DwSXPLzFL5H7fVp83_9t_IRgCE1p5atL8lxwJXJpdadneXapmyjIpjl6Vr8DPQ'
    HTTP/1.1 200 OK
    Date: Sun, 08 May 2022 17:06:54 GMT
    Content-Type: application/json
    Vary: Accept-Encoding
    Content-Length: 57
    
    {
      "id": "100",
      "username": "administrator",
      "name": "Big Boss"
    }

#### Cookie

If there is no query parameter, then the webapp looks for a JWT in the `token` cookie next:

    $ curl -XGET -H 'cookie: token=eyJ4NXQjUzI1NiI6IjNvWmU3cnowQ3hBSWJMNTlmM01ZZGdqNFIzS1NRc29fX01PUTd1YnJjZlUiLCJ4NWMiOlsiTUlJQ29qQ0NBWXFnQXdJQkFnSUdBWUNTQkFZd01BMEdDU3FHU0liM0RRRUJDd1VBTUJJeEVEQU9CZ05WQkFNTUIyVjRZVzF3YkdVd0hoY05Nakl3TlRBMU1ESXhPVE00V2hjTk1qTXdORE13TURJeE9UTTRXakFTTVJBd0RnWURWUVFEREFkbGVHRnRjR3hsTUlJQklqQU5CZ2txaGtpRzl3MEJBUUVGQUFPQ0FROEFNSUlCQ2dLQ0FRRUFtdkdZRWdwdlpYTnpKeE40RFFJRHUyMGtkaWwwcndab2JHMHVoNG9Lam5NMXBvUXd6ZURJdURGcFdXemxYekV4alFmWmlVRDR0TzROMWdcL2ZoR2xJWlROYkRaTkY1U25Xa0VkNEZ0T0l4TDZEMVZtWU02NjNzNVRoWGh6NVl0ejBGb1JoTG9nNGVWTk84aG5Jb1lxTGs2ZUhsaGRTQlo4RkhsTUtldU9yMXc4OGZOd0lPK1ZiUDRwM29wb25JQm1LYWNhblE5MGhWbUZpYlVnTXVQK2o1eFBVYXorVWtDOWVneXRLT29TYWMxd3BxMDVBdVl0ZWhONE5ZWGRDeFI3azdYaFJSZkRGUGVPZ3V5R21yRHF2blMxdGFLYVZOZm95Qk9jcU5rTmt1d3RITTFGOERkQmhzWDdhVDR0amY1Z21yZDJoMml1dHJGSlBQSit1M2E0MTh3SURBUUFCTUEwR0NTcUdTSWIzRFFFQkN3VUFBNElCQVFBd0xcL1B1a2RcL3JqbFQ1YTdlWlFkcmtUXC9KNEtyRE9YZXNRa0h4QnpvYTRZQVk3WmpkaHEwczRhYloxQnZNMHJ5c2JSeTdITFwvM0k2NEVPVUJjV1wvS2JRMnpLcGw5czdYM1Mza1wvcUlcL0dKOGNYcGRWOEtDSndieHB5UmNIdXRJMmFnTTcwQ0tVb0htRitqak9oaEpKTkU4ZjZ5RVFUcEFiWU1iTmo5ZmdsWFVlQTZhd1NmY2VzVG9RZER4dG9IaUU2dzZCVllwYlFPNEs3NWs2UURtR3E2QzlNTk16U21aRnZNUGFGSDhMa0syN2x3SHVZQ3lNZGJVXC93M3hnMnlDcnY0QzhSb1kwbFZUXC9IXC83RVNRVkNJdG5lV1hcL0Q5YzZTQ3F5azh3dlwvdkExTGloRGdlMU5wZ0dXcGlGWm1GM0plbkxrV0tiUUlCSkJNaCt0V3dHbCtHc0UiXSwia2lkIjoiMjAyMi0wNS0wNSIsImFsZyI6IlJTMjU2In0.eyJhY2NvdW50SWQiOiIxMDAiLCJhY2NvdW50TmFtZSI6IkJpZyBCb3NzIiwiYWNjb3VudFVzZXJuYW1lIjoiYWRtaW5pc3RyYXRvciIsImlzcyI6ImV4YW1wbGUiLCJleHAiOjE2NTIwMzMwNTYsImlhdCI6MTY1MjAyOTQ1NiwianRpIjoiMjFjZTdiNmItOTY0Ni00MzgyLTkxMjktNDJiZWQzNzM2NThmIn0.VfqznFUGm57-EqZtwHpAbiRgyCZ3VbIZfy7MLf1Jf-FedOdJj5-lVgCseGLJ3nciN5sZx8YVqIOpn14qnoO19rYdMiPLhd1kL5c7WGw6P80BLKBtsaBQn5DetVA7N0GokFjHRr1I73t6O-3w9rrRdkRHVWHawK2ahChncx0uegnep0UoCDvsl3JoKdyKP5SbTsSWm6y7Uu2v2e9FSmso8CazzEEMB0qdsvF5wC37DfwDKDgNml981wADEqmfgwZXtcVWjbZJpqht1SKS7ejt8vK-DwSXPLzFL5H7fVp83_9t_IRgCE1p5atL8lxwJXJpdadneXapmyjIpjl6Vr8DPQ' localhost:8080/v1/me
    HTTP/1.1 200 OK
    Content-Type: application/json

    {
      "id": "100",
      "username": "administrator",
      "name": "Big Boss"
    }

#### Authorization Bearer Token

If there is no query parameter or cookie, then the webapp looks for a JWT bearer token in the `Authorization` header.

    curl -XGET -H 'authorization: bearer eyJ4NXQjUzI1NiI6IjNvWmU3cnowQ3hBSWJMNTlmM01ZZGdqNFIzS1NRc29fX01PUTd1YnJjZlUiLCJ4NWMiOlsiTUlJQ29qQ0NBWXFnQXdJQkFnSUdBWUNTQkFZd01BMEdDU3FHU0liM0RRRUJDd1VBTUJJeEVEQU9CZ05WQkFNTUIyVjRZVzF3YkdVd0hoY05Nakl3TlRBMU1ESXhPVE00V2hjTk1qTXdORE13TURJeE9UTTRXakFTTVJBd0RnWURWUVFEREFkbGVHRnRjR3hsTUlJQklqQU5CZ2txaGtpRzl3MEJBUUVGQUFPQ0FROEFNSUlCQ2dLQ0FRRUFtdkdZRWdwdlpYTnpKeE40RFFJRHUyMGtkaWwwcndab2JHMHVoNG9Lam5NMXBvUXd6ZURJdURGcFdXemxYekV4alFmWmlVRDR0TzROMWdcL2ZoR2xJWlROYkRaTkY1U25Xa0VkNEZ0T0l4TDZEMVZtWU02NjNzNVRoWGh6NVl0ejBGb1JoTG9nNGVWTk84aG5Jb1lxTGs2ZUhsaGRTQlo4RkhsTUtldU9yMXc4OGZOd0lPK1ZiUDRwM29wb25JQm1LYWNhblE5MGhWbUZpYlVnTXVQK2o1eFBVYXorVWtDOWVneXRLT29TYWMxd3BxMDVBdVl0ZWhONE5ZWGRDeFI3azdYaFJSZkRGUGVPZ3V5R21yRHF2blMxdGFLYVZOZm95Qk9jcU5rTmt1d3RITTFGOERkQmhzWDdhVDR0amY1Z21yZDJoMml1dHJGSlBQSit1M2E0MTh3SURBUUFCTUEwR0NTcUdTSWIzRFFFQkN3VUFBNElCQVFBd0xcL1B1a2RcL3JqbFQ1YTdlWlFkcmtUXC9KNEtyRE9YZXNRa0h4QnpvYTRZQVk3WmpkaHEwczRhYloxQnZNMHJ5c2JSeTdITFwvM0k2NEVPVUJjV1wvS2JRMnpLcGw5czdYM1Mza1wvcUlcL0dKOGNYcGRWOEtDSndieHB5UmNIdXRJMmFnTTcwQ0tVb0htRitqak9oaEpKTkU4ZjZ5RVFUcEFiWU1iTmo5ZmdsWFVlQTZhd1NmY2VzVG9RZER4dG9IaUU2dzZCVllwYlFPNEs3NWs2UURtR3E2QzlNTk16U21aRnZNUGFGSDhMa0syN2x3SHVZQ3lNZGJVXC93M3hnMnlDcnY0QzhSb1kwbFZUXC9IXC83RVNRVkNJdG5lV1hcL0Q5YzZTQ3F5azh3dlwvdkExTGloRGdlMU5wZ0dXcGlGWm1GM0plbkxrV0tiUUlCSkJNaCt0V3dHbCtHc0UiXSwia2lkIjoiMjAyMi0wNS0wNSIsImFsZyI6IlJTMjU2In0.eyJhY2NvdW50SWQiOiIxMDAiLCJhY2NvdW50TmFtZSI6IkJpZyBCb3NzIiwiYWNjb3VudFVzZXJuYW1lIjoiYWRtaW5pc3RyYXRvciIsImlzcyI6ImV4YW1wbGUiLCJleHAiOjE2NTIwMzMwNTYsImlhdCI6MTY1MjAyOTQ1NiwianRpIjoiMjFjZTdiNmItOTY0Ni00MzgyLTkxMjktNDJiZWQzNzM2NThmIn0.VfqznFUGm57-EqZtwHpAbiRgyCZ3VbIZfy7MLf1Jf-FedOdJj5-lVgCseGLJ3nciN5sZx8YVqIOpn14qnoO19rYdMiPLhd1kL5c7WGw6P80BLKBtsaBQn5DetVA7N0GokFjHRr1I73t6O-3w9rrRdkRHVWHawK2ahChncx0uegnep0UoCDvsl3JoKdyKP5SbTsSWm6y7Uu2v2e9FSmso8CazzEEMB0qdsvF5wC37DfwDKDgNml981wADEqmfgwZXtcVWjbZJpqht1SKS7ejt8vK-DwSXPLzFL5H7fVp83_9t_IRgCE1p5atL8lxwJXJpdadneXapmyjIpjl6Vr8DPQ' localhost:8080/v1/me
    HTTP/1.1 200 OK
    Content-Type: application/json
    
    {
      "id": "100",
      "username": "administrator",
      "name": "Big Boss"
    }

### Unauthorized Request

If the user provides no credentials, then the webapp returns a `401 Unauthorized` response.

    curl -D - -XGET localhost:8080/v1/me
    HTTP/1.1 401 Unauthorized
    WWW-Authenticate: Bearer realm="example"
    Content-Type: text/plain
    
    Credentials are required to access this resource.

