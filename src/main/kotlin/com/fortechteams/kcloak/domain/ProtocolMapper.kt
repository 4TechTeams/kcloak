package com.fortechteams.kcloak.domain

interface ProtocolMapper {
  val name: String
  val protocol: MapperProtocol
  val protocolMapper: MapperType
}

enum class MapperProtocol(val protocolName: String) {
  OPENID_CONNECT("openid-connect")
}

enum class MapperType(val mapperTypename: String) {
  USER_ATTRIBUTE("oidc-usermodel-attribute-mapper")
}

data class UserAttributeMapper(
  override val name: String
) : ProtocolMapper {

  override val protocol: MapperProtocol = MapperProtocol.OPENID_CONNECT

  override val protocolMapper: MapperType = MapperType.USER_ATTRIBUTE
}
