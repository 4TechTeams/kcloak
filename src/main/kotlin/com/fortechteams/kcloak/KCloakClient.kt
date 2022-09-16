package com.fortechteams.kcloak

import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.KeycloakBuilder
import org.keycloak.representations.info.ServerInfoRepresentation
import com.fortechteams.kcloak.exception.BadExpectationException
import com.fortechteams.kcloak.exception.PermissionException

interface KCloakClient {

  /**
   * Returns an information object about the Keycloak server instance.
   *
   * @see ServerInfoRepresentation
   *
   * @throws PermissionException If a the user does not have permission to get server info
   */
  fun info(): ServerInfoRepresentation

  /**
   * Opens a [RealmClientImpl] for given realm
   *
   * If the realm does not exist, it will be created by default. However, you can disable automatic realm ceation by
   * setting [ClientSettings.createRealmIfNotExists] to `false` in client-settings. This can be helpful, if e.g. your
   * current client does not have permission to create realms. In the later case, a [BadExpectationException] will be
   * thrown.
   *
   * @throws BadExpectationException If the realm does not exist, but automatic realm creation was disabled.
   */
  fun realm(name: String): RealmClient

  companion object {

    fun of(keycloakInstance: Keycloak): KCloakClientImpl =
      KCloakClientImpl(keycloakInstance, ClientSettings())

    fun of(keycloakBuilder: KeycloakBuilder): KCloakClientImpl =
      of(keycloakBuilder.build())

    fun of(keycloakInstance: Keycloak, clientSettings: ClientSettings): KCloakClientImpl =
      KCloakClientImpl(keycloakInstance, clientSettings)

    fun of(keycloakInstance: Keycloak, settingsFn: ClientSettings.() -> Unit): KCloakClientImpl {
      val cs = ClientSettings()
      settingsFn(cs)

      return of(keycloakInstance, cs)
    }

    fun of(keycloakBuilder: KeycloakBuilder, settingsFn: ClientSettings.() -> Unit): KCloakClientImpl =
      of(keycloakBuilder.build(), settingsFn)
  }
}
