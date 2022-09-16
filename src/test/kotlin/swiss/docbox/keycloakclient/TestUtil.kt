package swiss.docbox.keycloakclient

import kotlin.test.assertNotNull

object TestUtil {

  fun KeycloakClient.assertIsUp() =
    assertNotNull(this.info().systemInfo.uptime)
}
