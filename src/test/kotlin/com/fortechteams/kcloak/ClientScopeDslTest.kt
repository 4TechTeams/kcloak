package com.fortechteams.kcloak

import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals

internal class ClientScopeDslTest {

  private val clientScopesDsl =
    KCloak
      .of(KCUtil.localKeycloakBuilder)
      .realm("ClientScopeDslTest${UUID.randomUUID()}")
      .clientScopes

  @Test
  fun update() {
    val name = "ClientScopeDslTest${UUID.randomUUID()}"
    val cs1 = clientScopesDsl.create(name){}

    cs1.update {
      description = "foo"
    }

    val res = clientScopesDsl.get(name)!!

    assertEquals(name, res.representation().name)
    assertEquals("foo", res.representation().description)
  }
}
