KCloak
======

*An opinionated Client implementation for Keycloak, the "k" stands for Kotlin*

![example workflow](https://github.com/4techteams/kcloak/actions/workflows/gradle.yml/badge.svg)

Back then, one dev in some software engineering team accidentally pronounced the famous identity provider 
"Kaycloak". Another guy who knew that Kotlin users tend to prefix their libraries with "k" came up with "k-cloak". Thats 
that... KCloak is, or at least started as a Kotlin Client for Keycloak, because the then-existing java implementation 
was very "java-like". KCloak abstracts over all these internals, boilerplate, missing error handling, and sometimes not 
well-documented calls, but still used the java client internally for maximum compatibility.

## Installation

**TBD**

## Usage

### Create an Instance

KCloak internally relies on the low-level java client of Keycloak. The easiest way to build a `Keycloak` instance, is
using the supplied builder:

```kotlin
val kc = KCloak.of(
  KeycloakBuilder.builder()
    .serverUrl("http://localhost:8090")
    .realm("master")
    .grantType(OAuth2Constants.PASSWORD)
    .clientId("admin-cli")
    .username("admin")
    .password("12345678")
)
```

Additionally, client settings can be changed. See
[ClientSettings](src/main/kotlin/com/fortechteams/kcloak/ClientSettings.kt) for reference:

```kotlin
val kc = KCloak.of(
  KeycloakBuilder.builder() //...
) {
  createRealmIfNotExists = false
}
```

