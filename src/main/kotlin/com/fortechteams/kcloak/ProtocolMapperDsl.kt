package com.fortechteams.kcloak

import com.fortechteams.kcloak.builder.ProtocolMapperBuilder
import com.fortechteams.kcloak.util.ExperimentalDsl
import org.keycloak.admin.client.resource.ProtocolMappersResource
import org.keycloak.representations.idm.ProtocolMapperRepresentation

@ExperimentalDsl
interface ProtocolMapperDsl {

  /**
   * Returns the representation of a given protocol mapper
   */
  fun representation(): ProtocolMapperRepresentation

  /**
   * Updates a protocol mappers properties
   */
  fun update(updateFn: ProtocolMapperRepresentation.() -> Unit)
  fun update(builder: ProtocolMapperBuilder)
}

class ProtocolMapperDslImpl(
  private val protocolMappersRes: ProtocolMappersResource,
  private val protocolMapperRep: ProtocolMapperRepresentation
) : ProtocolMapperDsl {

  override fun representation(): ProtocolMapperRepresentation =
    protocolMapperRep

  override fun update(updateFn: ProtocolMapperRepresentation.() -> Unit) {
    updateFn(protocolMapperRep)
    protocolMappersRes.update(protocolMapperRep.id, protocolMapperRep)
  }

  override fun update(builder: ProtocolMapperBuilder) =
    update {
      builder.modify(this)
    }
}
