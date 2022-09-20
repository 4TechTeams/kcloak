package com.fortechteams.kcloak

import com.fortechteams.kcloak.exception.PermissionException
import org.keycloak.admin.client.resource.ClientResource
import org.keycloak.representations.idm.ClientRepresentation
import javax.ws.rs.ForbiddenException
import javax.ws.rs.NotAllowedException

interface ClientDsl {

  /**
   * Returns a representation for the client
   */
  fun representation(): ClientRepresentation

  /**
   * Updates a clients properties
   */
  fun update(updateFn: ClientRepresentation.() -> Unit)
}

class ClientDslImpl(
  private val settings: Settings,
  private val clientRes: ClientResource
) : ClientDsl {

  override fun representation(): ClientRepresentation =
    try {
      clientRes.toRepresentation()
    } catch (e: ForbiddenException) {
      throw PermissionException(e)
    } catch (e: NotAllowedException) {
      throw PermissionException(e)
    }

  override fun update(updateFn: ClientRepresentation.() -> Unit) {
    val rep = representation()
    updateFn(rep)

    try {
      clientRes.update(rep)
    } catch (e: ForbiddenException) {
      throw PermissionException(e)
    } catch (e: NotAllowedException) {
      throw PermissionException(e)
    }
  }
}
