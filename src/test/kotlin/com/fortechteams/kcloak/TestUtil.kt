package com.fortechteams.kcloak

import com.fortechteams.kcloak.KCloakClientImpl
import kotlin.test.assertNotNull

object TestUtil {

  fun KCloakClientImpl.assertIsUp() =
    assertNotNull(this.info().systemInfo.uptime)
}
