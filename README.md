kcloak
======================

**An opinionated Client for Keycloak, the "k" stands for Kotlin**

## Installation

**TBD**

## Usage

### Create a Client

This client internally relies on the low-level java client of Keycloak. The easiest way to build a client instance, is
using the supplied builder:

```kotlin
val kc = KeycloakClient.of(
  KeycloakBuilder.builder()
    .serverUrl("http://localhost:8090")
    .realm("master")
    .grantType(OAuth2Constants.PASSWORD)
    .clientId("admin-cli")
    .username("admin")
    .password("12345678")
)
```

Additionally to the `Keycloak` instance, client settings can be changed. See
[ClientSettings](src/main/kotlin/swiss/docbox/keycloakclient/ClientSettings.kt) for reference:

```kotlin
val kc = KeycloakClient.of(
  KeycloakBuilder.builder() //...
) {
  createRealmIfNotExists = false
}
```

