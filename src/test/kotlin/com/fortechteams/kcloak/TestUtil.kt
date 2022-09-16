package com.fortechteams.kcloak

import kotlin.test.assertNotNull

object TestUtil {

  fun KCloakImpl.assertIsUp() =
    assertNotNull(this.info().systemInfo.uptime)
}
