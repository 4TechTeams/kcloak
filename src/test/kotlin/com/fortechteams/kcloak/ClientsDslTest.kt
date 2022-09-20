package com.fortechteams.kcloak

import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.*

internal class ClientsDslTest {

  private val kc by lazy {
    KCloak.of(KCUtil.localKeycloakBuilder)
  }

  private val clientsDsl =
    KCloak
      .of(KCUtil.localKeycloakBuilder)
      .realm("clients-dsl-test_${UUID.randomUUID()}")
      .clients

  @Test
  fun `get or create`() {
    val name = "client_${UUID.randomUUID()}"
    val c1 = clientsDsl.getOrCreate(name)
    val rep = c1.representation()

    assertEquals(name, rep.clientId)
    assertFalse(rep.isEnabled)
  }

  @Test
  fun `get representation and dsl should return null if not exists`() {
    assertNull(clientsDsl.get("does-not-exist_1"))
    assertNull(clientsDsl.getRepresentation("does-not-exist_2"))
  }

  @Test
  fun `explicit create with exists and get`() {
    val name = "client_${UUID.randomUUID()}"

    clientsDsl.create(name) {
      isEnabled = true
      attributes = mapOf("bar" to "baz")
    }

    assertTrue(clientsDsl.exists(name))

    val rep = clientsDsl.getRepresentation(name)

    assertNotNull(rep)
    assertTrue(rep.isEnabled)
    assertNotNull(rep.attributes["bar"])
    assertEquals("baz", rep.attributes["bar"]!!)
  }
}
