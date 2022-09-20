package com.fortechteams.kcloak

import com.fortechteams.kcloak.exception.PermissionException
import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.KeycloakBuilder
import org.keycloak.representations.info.ServerInfoRepresentation
import org.slf4j.LoggerFactory
import javax.ws.rs.ForbiddenException

interface KCloak {

  /**
   * Provides a [RealmsDsl]
   */
  val realms: RealmsDsl

  /**
   * Returns an information object about the Keycloak server instance.
   *
   * @see ServerInfoRepresentation
   *
   * @throws PermissionException If a the user does not have permission to get server info
   */
  fun info(): ServerInfoRepresentation

  /**
   * Opens a [RealmDsl] for a given realm
   *
   * if the realm does not exist, it will be created automatically. This is actually santactic sugar for
   * [RealmsDsl.getOrCreate]
   */
  fun realm(name: String): RealmDsl

  companion object {

    fun of(keycloakInstance: Keycloak): KCloak =
      KCloakImpl(keycloakInstance)

    fun of(keycloakBuilder: KeycloakBuilder): KCloak =
      of(keycloakBuilder.build())
  }
}

class KCloakImpl(
  private val kc: Keycloak
) : KCloak {

  private val log = LoggerFactory.getLogger(javaClass)

  override val realms: RealmsDsl by lazy {
    RealmsDslImpl(kc.realms())
  }

  override fun info(): ServerInfoRepresentation =
    try {
      kc.serverInfo().info
    } catch (e: ForbiddenException) {
      throw PermissionException(e)
    }

  override fun realm(name: String): RealmDsl =
    realms.getOrCreate(name)
}
