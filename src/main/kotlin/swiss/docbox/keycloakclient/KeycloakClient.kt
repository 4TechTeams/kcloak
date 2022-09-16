package swiss.docbox.keycloakclient

import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.KeycloakBuilder
import org.keycloak.representations.idm.RealmRepresentation
import org.keycloak.representations.info.ServerInfoRepresentation
import org.slf4j.LoggerFactory
import swiss.docbox.keycloakclient.exception.BadExpectationException
import swiss.docbox.keycloakclient.exception.PermissionException
import swiss.docbox.keycloakclient.exception.StateException
import javax.ws.rs.ForbiddenException
import javax.ws.rs.NotFoundException

class KeycloakClient private constructor(
  private val kc: Keycloak,
  private val clientSettings: ClientSettings
) {

  private val log = LoggerFactory.getLogger(javaClass)

  /**
   * Returns an information object about the Keycloak server instance.
   *
   * @see ServerInfoRepresentation
   *
   * @throws PermissionException If a the user does not have permission to get server info
   */
  fun info(): ServerInfoRepresentation =
    try {
      kc.serverInfo().info
    } catch (e: ForbiddenException) {
      throw PermissionException(e)
    }

  /**
   * Opens a [RealmClient] for given realm
   *
   * If the realm does not exist, it will be created by default. However, you can disable automatic realm ceation by
   * setting [ClientSettings.createRealmIfNotExists] to `false` in client-settings. This can be helpful, if e.g. your
   * current client does not have permission to create realms. In the later case, a [BadExpectationException] will be
   * thrown.
   *
   * @throws BadExpectationException If the realm does not exist, but automatic realm creation was disabled.
   */
  fun realm(name: String): RealmClient {
    val existing = kc.realms().findAll().find { it.realm == name } != null

    if (!existing) {
      if (clientSettings.createRealmIfNotExists) {
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

      return RealmClient(kc, clientSettings, realmRes)

    } catch (e: NotFoundException) {
      throw StateException("Requested realm $name could not be fetched from Keycloak instance!", e)
    }
  }

  companion object {

    fun of(keycloakInstance: Keycloak): KeycloakClient =
      KeycloakClient(keycloakInstance, ClientSettings())

    fun of(keycloakBuilder: KeycloakBuilder): KeycloakClient =
      of(keycloakBuilder.build())

    fun of(keycloakInstance: Keycloak, clientSettings: ClientSettings): KeycloakClient =
      KeycloakClient(keycloakInstance, clientSettings)

    fun of(keycloakInstance: Keycloak, settingsFn: ClientSettings.() -> Unit): KeycloakClient {
      val cs = ClientSettings()
      settingsFn(cs)

      return of(keycloakInstance, cs)
    }

    fun of(keycloakBuilder: KeycloakBuilder, settingsFn: ClientSettings.() -> Unit): KeycloakClient =
      of(keycloakBuilder.build(), settingsFn)
  }
}

class ClientSettings {

  var createRealmIfNotExists: Boolean = true
}
