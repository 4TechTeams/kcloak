package com.fortechteams.kcloak

import org.keycloak.representations.idm.RealmRepresentation
import javax.ws.rs.ForbiddenException

interface RealmClient {

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
