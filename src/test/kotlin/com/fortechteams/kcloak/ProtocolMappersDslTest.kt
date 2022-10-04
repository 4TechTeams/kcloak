package com.fortechteams.kcloak


import com.fortechteams.kcloak.builder.OidcUserModelAttributeMapper
import com.fortechteams.kcloak.builder.ProtocolMapperConstants
import com.fortechteams.kcloak.util.ExperimentalDsl
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals

@OptIn(ExperimentalDsl::class)
internal class ProtocolMappersDslTest {

  private val protocolMappersDsl =
    KCloak
      .of(KCUtil.localKeycloakBuilder)
      .realm("ProtocolMappersDslTest${UUID.randomUUID()}")
      .clientScopes
      .getOrCreate("ProtocolMappersDslTest${UUID.randomUUID()}")
      .protocolMappers

  @Test
  fun `create using builder`() {
    val mapperName = "ProtocolMappersDslTest_mapper${UUID.randomUUID()}"
    val pm = protocolMappersDsl.create(
      mapperName,
      OidcUserModelAttributeMapper(
        userAttributeName = "test-attr",
        accessTokenClaim = true
      )
    )

    val rep = pm.representation()
    assertEquals(mapperName, pm.representation().name)
    assertEquals(ProtocolMapperConstants.Protocol.OIDC.protocolName, rep.protocol)
    assertEquals(ProtocolMapperConstants.MapperType.USERMODEL_ATTRIBUTE.typeName, rep.protocolMapper)
    assertEquals("true", rep.config["access.token.claim"])
  }
}
