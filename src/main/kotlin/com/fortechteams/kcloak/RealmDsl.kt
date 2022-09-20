package com.fortechteams.kcloak

import com.fortechteams.kcloak.exception.PermissionException
import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.resource.RealmResource
import org.keycloak.representations.idm.RealmRepresentation
import javax.ws.rs.ForbiddenException
import javax.ws.rs.NotAllowedException

interface RealmDsl {

  /**
   * Returns an instance of [ClientsDsl] for given realm
   */
  val clients: ClientsDsl

  /**
   * Returns an instance of [ClientScopesDsl] for given realm
   */
  val clientScopes: ClientScopesDsl

  /**
   * Returns a representation fo the realm
   */
  fun representation(): RealmRepresentation

  /**
   * Updates settings of the current realm
   *
   * @throws ForbiddenException If user does not have permission to change realm settings
   */
  fun update(updateFn: RealmRepresentation.() -> Unit)

  /**
   * Enables the realm
   *
   * @throws ForbiddenException If user does not have permission to change realm settings
   */
  fun enable()

  /**
   * Disables the realm
   *
   * @throws ForbiddenException If user does not have permission to change realm settings
   */
  fun disable()
}

class RealmDslImpl(
  private val realmRes: RealmResource,
  private val realmRep: RealmRepresentation? = null
) : RealmDsl {

  override val clients: ClientsDsl by lazy {
    ClientsDslImpl(realmRes.clients())
  }

  override val clientScopes: ClientScopesDsl by lazy {
    ClientScopesDslImpl(realmRes.clientScopes())
  }

  override fun representation(): RealmRepresentation =
    realmRep ?: realmRes.toRepresentation()

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
