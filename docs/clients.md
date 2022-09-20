Clients
=======

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

## Auto Creation

When calling `clientsDsl("client-foo2")`, by default, the client will be auto-created if it doesn't exist. This can be
disabled by setting the `createClientIfNotExists` setting to `false`.
