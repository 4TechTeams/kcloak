package com.fortechteams.kcloak.builder

import org.keycloak.representations.idm.ProtocolMapperRepresentation

fun interface ProtocolMapperBuilder {

  fun modify(pm: ProtocolMapperRepresentation)

}

data class OidcUserModelAttributeMapper(
  val userAttributeName: String,
  val claimName: String = userAttributeName,
  val idTokenClaim: Boolean = false,
  val accessTokenClaim: Boolean = false,
  val userInfoTokenClaim: Boolean = true,
  val jsonLabelType: ProtocolMapperConstants.JsonLabelType = ProtocolMapperConstants.JsonLabelType.STRING
): ProtocolMapperBuilder {

  override fun modify(pm: ProtocolMapperRepresentation) {
    pm.protocol = ProtocolMapperConstants.Protocol.OIDC.protocolName
    pm.protocolMapper = ProtocolMapperConstants.MapperType.USERMODEL_ATTRIBUTE.typeName
    pm.config = mapOf(
      "userinfo.token.claim" to if(userInfoTokenClaim) "true" else "false",
      "user.attribute" to userAttributeName,
      "id.token.claim" to if(idTokenClaim) "true" else "false",
      "access.token.claim" to if(accessTokenClaim) "true" else "false",
      "claim.name" to claimName,
      "jsonType.label" to jsonLabelType.labelTypeName
    )
  }

}
