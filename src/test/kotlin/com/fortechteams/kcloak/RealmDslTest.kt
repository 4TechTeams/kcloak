package com.fortechteams.kcloak

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*

internal class RealmDslTest {

  private val kc by lazy {
    KCloak.of(KCUtil.localKeycloakBuilder)
  }

  @Test
  fun `update realm settings`() {
    val realmName = "RealmDslTest_${UUID.randomUUID()}"
    val realm = kc.realm(realmName)

    realm.update {
      isEnabled = true
      displayName = "Some name..."
      isResetPasswordAllowed = false
      isDuplicateEmailsAllowed = true
    }

    val rep = realm.representation()
    assertTrue(rep.isEnabled)
    assertEquals("Some name...", rep.displayName)
    assertFalse(rep.isResetPasswordAllowed)
    assertTrue(rep.isDuplicateEmailsAllowed)
  }

}
