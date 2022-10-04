package com.fortechteams.kcloak

import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class ClientScopesDslTest {

  private val clientScopesDsl =
    KCloak
      .of(KCUtil.localKeycloakBuilder)
      .realm("ClientScopesDslTest${UUID.randomUUID()}")
      .clientScopes

  @Test
  fun `get or create`() {
    val name = "ClientScopesDslTest${UUID.randomUUID()}"
    val c1 = clientScopesDsl.getOrCreate(name)
    val rep = c1.representation()

    assertEquals(name, rep.name)
  }

  @Test
  fun `get representation and dsl should return null if not exists`() {
    assertNull(clientScopesDsl.get("does-not-exist_1"))
    assertNull(clientScopesDsl.getRepresentation("does-not-exist_2"))
  }

  @Test
  fun `explicit create with exists and get`() {
    val name = "ClientScopesDslTest${UUID.randomUUID()}"

    clientScopesDsl.create(name) {
      description = "foo"
      attributes = mapOf("bar" to "baz")
    }

    assertTrue(clientScopesDsl.exists(name))

    val rep = clientScopesDsl.getRepresentation(name)

    assertNotNull(rep)
    assertEquals("foo", rep.description)
    assertNotNull(rep.attributes["bar"])
    assertEquals("baz", rep.attributes["bar"]!!)
  }
}
