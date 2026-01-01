# FHIR Authentication Library

A Java library for authenticating requests to FHIR servers using the
[SMART Backend Services](https://hl7.org/fhir/smart-app-launch/backend-services.html)
specification. Supports OAuth2 client credentials grant with both symmetric (client
secret) and asymmetric (private key JWT) authentication methods.

## Features

- SMART configuration discovery for automatic token endpoint detection
- Symmetric authentication using client ID and secret
- Asymmetric authentication using private key JWTs (RSA and ECDSA)
- Token caching with configurable expiry tolerance
- Integration with Apache HttpClient via request interceptor

## Installation

### Prerequisites

- Java 11 or higher
- Maven 3.6 or higher

### Maven

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>au.csiro.fhir</groupId>
    <artifactId>fhir-auth</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Usage

### Configuration

Create an `AuthConfig` with your authentication settings:

```java
// Symmetric authentication (client secret)
AuthConfig config = AuthConfig.builder()
    .enabled(true)
    .useSMART(true)  // Discover token endpoint from FHIR server
    .clientId("your-client-id")
    .clientSecret("your-client-secret")
    .scope("system/*.read")
    .tokenExpiryTolerance(120)  // Refresh token 2 minutes before expiry
    .build();

// Asymmetric authentication (private key JWT)
AuthConfig config = AuthConfig.builder()
    .enabled(true)
    .useSMART(true)
    .clientId("your-client-id")
    .privateKeyJWK("{\"kty\":\"RSA\",\"kid\":\"...\",\"n\":\"...\",\"e\":\"...\",\"d\":\"...\"}")
    .scope("system/*.read")
    .build();

// Without SMART discovery (explicit token endpoint)
AuthConfig config = AuthConfig.builder()
    .enabled(true)
    .useSMART(false)
    .tokenEndpoint("https://auth.example.com/oauth2/token")
    .clientId("your-client-id")
    .clientSecret("your-client-secret")
    .build();
```

### Using with Apache HttpClient

The library provides `TokenAuthRequestInterceptor` and `TokenCredentials` for
integration with Apache HttpClient:

```java
URI fhirEndpoint = URI.create("https://fhir.example.com/r4");

// Create the credential factory
try (SMARTTokenCredentialFactory factory = SMARTTokenCredentialFactory.create(config)) {
    // Get credentials for the FHIR endpoint
    Optional<TokenCredentials> credentials = factory.createCredentials(fhirEndpoint, config);

    if (credentials.isPresent()) {
        // Configure credentials provider
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(
            new AuthScope(fhirEndpoint.getHost(), fhirEndpoint.getPort()),
            credentials.get()
        );

        // Build HttpClient with token interceptor
        CloseableHttpClient httpClient = HttpClients.custom()
            .setDefaultCredentialsProvider(credentialsProvider)
            .addInterceptorFirst(new TokenAuthRequestInterceptor())
            .build();

        // Use the client for authenticated requests
        HttpGet request = new HttpGet(fhirEndpoint.resolve("/Patient"));
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            // Handle response
        }
    }
}
```

### Configuration options

| Option                 | Type    | Default | Description                                             |
|------------------------|---------|---------|---------------------------------------------------------|
| `enabled`              | boolean | `false` | Enable authentication                                   |
| `useSMART`             | boolean | `true`  | Discover token endpoint via SMART configuration         |
| `tokenEndpoint`        | String  | null    | OAuth2 token endpoint (required if `useSMART` is false) |
| `clientId`             | String  | null    | OAuth2 client ID (required if enabled)                  |
| `clientSecret`         | String  | null    | Client secret for symmetric auth                        |
| `privateKeyJWK`        | String  | null    | Private key in JWK format for asymmetric auth           |
| `useFormForBasicAuth`  | boolean | `false` | Send credentials in form body instead of header         |
| `scope`                | String  | null    | OAuth2 scope                                            |
| `tokenExpiryTolerance` | long    | `120`   | Seconds before expiry to refresh token                  |

Either `clientSecret` or `privateKeyJWK` must be provided when authentication is
enabled.

## Contributing

Contributions are welcome! Please see [CONTRIBUTING.md](CONTRIBUTING.md) for
guidelines.

## Licence

Copyright © 2026 Commonwealth Scientific and Industrial Research Organisation
(CSIRO) ABN 41 687 119 230. Licensed under the
[Apache License, Version 2.0](LICENSE).
