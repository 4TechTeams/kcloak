package com.fortechteams.kcloak

import com.fortechteams.kcloak.TestUtil.assertIsUp
import com.fortechteams.kcloak.exception.BadExpectationException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*
import kotlin.test.assertTrue

internal class KCloakTest {

  private val kc by lazy {
    KCloak.of(KCUtil.localKeycloakBuilder)
  }

  @Test
  fun `builds client from builder with default settings`() {
    KCloak
      .of(KCUtil.localKeycloakBuilder)
      .assertIsUp()
  }

  @Test
  fun `builds client from instance with default settings`() {
    KCloak
      .of(KCUtil.localKeycloakBuilder.build())
      .assertIsUp()
  }

  @Test
  fun `get realm client for missing realm with auto creation`() {
    val realmName = "KCloakTest_${UUID.randomUUID()}"
    val realm = kc.realm(realmName)

    assertEquals(realmName, realm.representation().realm)
    // auto-created realm should not be enabled
    assertFalse(realm.representation().isEnabled)
  }
}
