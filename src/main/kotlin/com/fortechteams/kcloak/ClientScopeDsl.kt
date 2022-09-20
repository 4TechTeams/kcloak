package com.fortechteams.kcloak

import com.fortechteams.kcloak.exception.PermissionException
import org.keycloak.admin.client.resource.ClientScopeResource
import org.keycloak.representations.idm.ClientScopeRepresentation
import javax.ws.rs.ForbiddenException
import javax.ws.rs.NotAllowedException

interface ClientScopeDsl {

  /**
   * Returns the representation of a given client scope
   */
  fun representation(): ClientScopeRepresentation

  /**
   * Updates a client scopes properties
   */
  fun update(updateFn: ClientScopeRepresentation.() -> Unit)

  /**
   * Finds or creates a protocol mapper
   */
  fun protocolMapper(name: String): ProtocolMapperDsl
}

class ClientScopeDslImpl(
  private val settings: Settings,
  private val clientScopeResource: ClientScopeResource
) : ClientScopeDsl {

  override fun representation(): ClientScopeRepresentation =
    try {
      clientScopeResource.toRepresentation()
    } catch (e: ForbiddenException) {
      throw PermissionException(e)
    } catch (e: NotAllowedException) {
      throw PermissionException(e)
    }

  override fun update(updateFn: ClientScopeRepresentation.() -> Unit) {
    val rep = representation()
    updateFn(rep)

    try {
      clientScopeResource.update(rep)
    } catch (e: ForbiddenException) {
      throw PermissionException(e)
    } catch (e: NotAllowedException) {
      throw PermissionException(e)
    }
  }

  override fun protocolMapper(name: String): ProtocolMapperDsl {
    TODO("Not yet implemented")
  }
}
