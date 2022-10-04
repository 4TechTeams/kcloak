package com.fortechteams.kcloak.builder

object ProtocolMapperConstants {

  enum class Protocol(val protocolName: String) {
    OIDC("openid-connect")
  }

  enum class MapperType(val typeName: String) {
    USERMODEL_ATTRIBUTE("oidc-usermodel-attribute-mapper")
  }

  enum class JsonLabelType(val labelTypeName: String) {
    STRING("String")
  }
}
