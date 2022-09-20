Usage
=====

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
[Settings](../src/main/kotlin/com/fortechteams/kcloak/Settings.kt) for reference:

```kotlin
val kc = KCloak.of(
  KeycloakBuilder.builder() //...
) {
  createRealmIfNotExists = false
}
```

*More examples can be found in [KCloakTest](../src/test/kotlin/com/fortechteams/kcloak/KCloakTest.kt)*
