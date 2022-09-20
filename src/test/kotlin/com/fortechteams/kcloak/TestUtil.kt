package com.fortechteams.kcloak

import kotlin.test.assertNotNull

object TestUtil {

  fun KCloak.assertIsUp() =
    assertNotNull(this.info().systemInfo.uptime)
}
