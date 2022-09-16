package swiss.docbox.keycloakclient

import org.keycloak.admin.client.Keycloak
import org.keycloak.representations.idm.RealmRepresentation
import org.slf4j.LoggerFactory
import swiss.docbox.keycloakclient.exception.BadExpectationException
import swiss.docbox.keycloakclient.exception.StateException
import javax.ws.rs.NotFoundException

class KeycloakClient private constructor(
  private val kc: Keycloak,
  private val clientSettings: ClientSettings
) {

  private val log = LoggerFactory.getLogger(javaClass)

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

    fun of(keycloakInstance: Keycloak, settings: ClientSettings = ClientSettings.default) =
      KeycloakClient(keycloakInstance, settings)
  }
}

data class ClientSettings(
  val createRealmIfNotExists: Boolean
) {
  companion object {
    val default = ClientSettings(
      createRealmIfNotExists = true
    )
  }
}
