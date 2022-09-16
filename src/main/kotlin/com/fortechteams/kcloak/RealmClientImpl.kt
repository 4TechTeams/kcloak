package com.fortechteams.kcloak

import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.resource.RealmResource
import org.keycloak.representations.idm.RealmRepresentation
import com.fortechteams.kcloak.exception.PermissionException
import javax.ws.rs.ForbiddenException
import javax.ws.rs.NotAllowedException

class RealmClientImpl(
  private val kc: Keycloak,
  private val clientSettings: ClientSettings,
  private val realmRes: RealmResource
) : RealmClient {

  override fun representation(): RealmRepresentation =
    realmRes.toRepresentation()

  override fun update(updateFn: RealmRepresentation.() -> Unit) {
    val rep = representation()
    updateFn(rep)

    try {
      realmRes.update(rep)
    } catch (e: ForbiddenException) {
      throw PermissionException(e)
    } catch (e: NotAllowedException) {
      throw PermissionException(e)
    }
  }

  override fun enable() =
    update {
      isEnabled = true
    }

  override fun disable() =
    update {
      isEnabled = true
    }
}
