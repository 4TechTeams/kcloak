KCloak
======

*An opinionated Client implementation for Keycloak, the "k" stands for Kotlin*

![CI Status](https://github.com/4techteams/kcloak/actions/workflows/gradle.yml/badge.svg)

Back then, one dev in some software engineering team accidentally pronounced the famous identity provider
"Kaycloak". Another guy who knew that Kotlin users tend to prefix their libraries with "k" came up with "k-cloak". Thats
that... KCloak is, or at least started as a Kotlin Client for Keycloak, because the then-existing java implementation
was very "java-like". KCloak abstracts over all these internals, boilerplate, missing error handling, and sometimes not
well-documented calls, but still used the java client internally for maximum compatibility.

## Installation

**TBD**

## Usage

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

*More examples can be found in [KCloakTest](src/test/kotlin/com/fortechteams/kcloak/KCloakTest.kt)*

### Realms

> A realm manages a set of users, credentials, roles, and groups. A user belongs to and logs into a realm. Realms are
> isolated from one another and can only manage and authenticate the users that they control.
> – [Core concepts and terms](https://www.keycloak.org/docs/latest/server_admin/#core-concepts-and-terms)

The following call resolves a realm and returns a [RealmDsl](src/main/kotlin/com/fortechteams/kcloak/RealmDsl.kt)
instance for it:

```kotlin
val kc = KCloak.of(TODO())
val masterRealm = ky.realm("master")

// get realm info 
masterRealm.info()
```

***Note:** As stated in the concept, most functionality is not global, but based on a realm.*

#### Auto Creation

When calling `kc.realm("foo")`, by default, the realm will be auto-created if it doesn't exist. This can be disabled by
setting the `createRealmIfNotExists` setting to `false`.

#### Updates

Realm settings can be updated with the following call:

```kotlin
masterRealm.update {
  isEnabled = true
  displayName = "Some name..."
  isResetPasswordAllowed = false
  isDuplicateEmailsAllowed = true
}
```

*More examples can be found in [RealmDslTest](src/test/kotlin/com/fortechteams/kcloak/RealmDslTest.kt)*

### Clients

> Clients are entities that can request Keycloak to authenticate a user. Most often, clients are applications and
> services that want to use Keycloak to secure themselves and provide a single sign-on solution. Clients can also be
> entities that just want to request identity information or an access token so that they can securely invoke other
> services on the network that are secured by Keycloak.
> – [Core concepts and terms](https://www.keycloak.org/docs/latest/server_admin/#core-concepts-and-terms)

```kotlin
val clientsDsl = KCloak
  .of(TODO())
  .realm("clients-dsl-test_${UUID.randomUUID()}")
  .clients

// get or create a client - will be auto-created if not exists
val c1 = clientsDsl("client-foo")

clientsDsl.create("client-foo2") {
  isEnabled = true
  attributes = mapOf("bar" to "baz")
}

val c2 = clientsDsl("client-foo2")

// explicitly create a client
```

The example above uses the invoke-syntax (`clientsDsl("...")`) which is an alias of `clientsDsl.get("...")`. It's just a
matter of style.

#### Auto Creation

When calling `clientsDsl("client-foo2")`, by default, the client will be auto-created if it doesn't exist. This can be
disabled by setting the `createClientIfNotExists` setting to `false`.
