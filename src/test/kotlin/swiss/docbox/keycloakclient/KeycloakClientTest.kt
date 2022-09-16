package swiss.docbox.keycloakclient

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import swiss.docbox.keycloakclient.TestUtil.assertIsUp
import swiss.docbox.keycloakclient.exception.BadExpectationException
import java.util.*
import kotlin.test.assertTrue

internal class KeycloakClientTest {

  private val client by lazy {
    KeycloakClient.of(KCUtil.localKeycloakBuilder)
  }

  @Test
  fun `builds client from builder with default settings`() {
    KeycloakClient
      .of(KCUtil.localKeycloakBuilder)
      .assertIsUp()
  }

  @Test
  fun `builds client from instance with default settings`() {
    KeycloakClient
      .of(KCUtil.localKeycloakBuilder.build())
      .assertIsUp()
  }

  @Test
  fun `get realm client for existing realm`() {
    val realmName = "master"
    val realm = client.realm(realmName)

    assertEquals(realmName, realm.representation().realm)
    assertTrue(realm.representation().isEnabled)
  }

  @Test
  fun `get realm client for missing realm with auto creation`() {
    val realmName = "does-not-exist_${UUID.randomUUID()}"
    val realm = client.realm(realmName)

    assertEquals(realmName, realm.representation().realm)
    // auto-created realm should not be enabled
    assertFalse(realm.representation().isEnabled)
  }

  @Test
  fun `get realm client for missing realm with disabled auto creation`() {
    val realmName = "does-not-exist_${UUID.randomUUID()}"

    assertThrows<BadExpectationException> {
      val c = KeycloakClient.of(KCUtil.localKeycloakBuilder) {
        createRealmIfNotExists = false
      }

      c.realm(realmName)
    }
  }
}
