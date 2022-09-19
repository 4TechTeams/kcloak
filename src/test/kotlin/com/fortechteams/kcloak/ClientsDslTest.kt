package com.fortechteams.kcloak

import com.fortechteams.kcloak.exception.BadExpectationException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

internal class ClientsDslTest {

  private val kc by lazy {
    KCloak.of(KCUtil.localKeycloakBuilder)
  }

  @Test
  fun `get and auto-create client`() {
    val clientDsl = kc
      .realm("clients-dsl-test_${UUID.randomUUID()}")
      .clients

    val c1 = clientDsl("foo1")

    assertEquals("foo1", c1.representation().clientId)
  }

  @Test
  fun `get not existing with auto-create disabled`() {
    val clientDsl = KCloak
      .of(KCUtil.localKeycloakBuilder) {
        createClientIfNotExists = false
      }
      .realm("clients-dsl-test_${UUID.randomUUID()}")
      .clients

    assertThrows<BadExpectationException> {
      clientDsl("foo1")
    }
  }

  @Test
  fun `create and get`() {
    val clientDsl = KCloak
      .of(KCUtil.localKeycloakBuilder) {
        createClientIfNotExists = false
      }
      .realm("clients-dsl-test_${UUID.randomUUID()}")
      .clients

    clientDsl.create("foo2") {
      isEnabled = true
      attributes = mapOf("bar" to "baz")
    }

    val c2 = clientDsl.get("foo2").representation()

    assertTrue(c2.isEnabled)
    assertNotNull(c2.attributes["bar"])
    assertEquals("baz", c2.attributes["bar"]!!)
  }
}
