package com.fortechteams.kcloak

import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.KeycloakBuilder
import org.keycloak.representations.info.ServerInfoRepresentation
import com.fortechteams.kcloak.exception.BadExpectationException
import com.fortechteams.kcloak.exception.PermissionException
import com.fortechteams.kcloak.exception.StateException
import org.keycloak.representations.idm.RealmRepresentation
import org.slf4j.LoggerFactory
import javax.ws.rs.ForbiddenException
import javax.ws.rs.NotFoundException

interface KCloak {

  /**
   * Returns an information object about the Keycloak server instance.
   *
   * @see ServerInfoRepresentation
   *
   * @throws PermissionException If a the user does not have permission to get server info
   */
  fun info(): ServerInfoRepresentation

  /**
   * Opens a [RealmDslImpl] for given realm
   *
   * If the realm does not exist, it will be created by default. However, you can disable automatic realm ceation by
   * setting [Settings.createRealmIfNotExists] to `false` in client-settings. This can be helpful, if e.g. your
   * current client does not have permission to create realms. In the later case, a [BadExpectationException] will be
   * thrown.
   *
   * @throws BadExpectationException If the realm does not exist, but automatic realm creation was disabled.
   */
  fun realm(name: String): RealmDsl

  companion object {

    fun of(keycloakInstance: Keycloak): KCloak =
      KCloakImpl(keycloakInstance, Settings())

    fun of(keycloakBuilder: KeycloakBuilder): KCloak =
      of(keycloakBuilder.build())

    fun of(keycloakInstance: Keycloak, settings: Settings): KCloak =
      KCloakImpl(keycloakInstance, settings)

    fun of(keycloakInstance: Keycloak, settingsFn: Settings.() -> Unit): KCloak {
      val cs = Settings()
      settingsFn(cs)

      return of(keycloakInstance, cs)
    }

    fun of(keycloakBuilder: KeycloakBuilder, settingsFn: Settings.() -> Unit): KCloak =
      of(keycloakBuilder.build(), settingsFn)
  }
}

class KCloakImpl(
  private val kc: Keycloak,
  private val settings: Settings
) : KCloak {

  private val log = LoggerFactory.getLogger(javaClass)

  override fun info(): ServerInfoRepresentation =
    try {
      kc.serverInfo().info
    } catch (e: ForbiddenException) {
      throw PermissionException(e)
    }

  override fun realm(name: String): RealmDslImpl {
    val existing = kc.realms().findAll().find { it.realm == name } != null

    if (!existing) {
      if (settings.createRealmIfNotExists) {
        log.debug("Realm $name does not exist, creating it...")
        val rep = RealmRepresentation()
        rep.realm = name

        kc.realms().create(rep)
      } else {
        throw BadExpectationException("Realm with name $name does not exist, and setting 'createRealmIfNotExists' has been set to false!")
      }
    }

    try {
      val realmRes = kc.realms().realm(name)

      return RealmDslImpl(kc, settings, realmRes)

    } catch (e: NotFoundException) {
      throw StateException("Requested realm $name could not be fetched from Keycloak instance!", e)
    }
  }
}
