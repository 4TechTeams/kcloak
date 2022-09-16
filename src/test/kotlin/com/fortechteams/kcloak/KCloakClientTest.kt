package com.fortechteams.kcloak

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import com.fortechteams.kcloak.TestUtil.assertIsUp
import com.fortechteams.kcloak.exception.BadExpectationException
import java.util.*
import kotlin.test.assertTrue

internal class KCloakClientTest {

  private val kc by lazy {
    KCloakClient.of(KCUtil.localKeycloakBuilder)
  }

  @Test
  fun `builds client from builder with default settings`() {
    KCloakClient
      .of(KCUtil.localKeycloakBuilder)
      .assertIsUp()
  }

  @Test
  fun `builds client from instance with default settings`() {
    KCloakClient
      .of(KCUtil.localKeycloakBuilder.build())
      .assertIsUp()
  }

  @Test
  fun `get realm client for existing realm`() {
    val realmName = "master"
    val realm = kc.realm(realmName)

    assertEquals(realmName, realm.representation().realm)
    assertTrue(realm.representation().isEnabled)
  }

  @Test
  fun `get realm client for missing realm with auto creation`() {
    val realmName = "does-not-exist_${UUID.randomUUID()}"
    val realm = kc.realm(realmName)

    assertEquals(realmName, realm.representation().realm)
    // auto-created realm should not be enabled
    assertFalse(realm.representation().isEnabled)
  }

  @Test
  fun `get realm client for missing realm with disabled auto creation`() {
    val realmName = "does-not-exist_${UUID.randomUUID()}"

    assertThrows<BadExpectationException> {
      val c = KCloakClient.of(KCUtil.localKeycloakBuilder) {
        createRealmIfNotExists = false
      }

      c.realm(realmName)
    }
  }
}
