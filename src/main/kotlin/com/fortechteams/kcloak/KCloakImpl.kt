package com.fortechteams.kcloak

import org.keycloak.admin.client.Keycloak
import org.keycloak.representations.idm.RealmRepresentation
import org.keycloak.representations.info.ServerInfoRepresentation
import org.slf4j.LoggerFactory
import com.fortechteams.kcloak.exception.BadExpectationException
import com.fortechteams.kcloak.exception.PermissionException
import com.fortechteams.kcloak.exception.StateException
import javax.ws.rs.ForbiddenException
import javax.ws.rs.NotFoundException

class KCloakImpl(
  private val kc: Keycloak,
  private val clientSettings: ClientSettings
) : KCloak {

  private val log = LoggerFactory.getLogger(javaClass)

  override fun info(): ServerInfoRepresentation =
    try {
      kc.serverInfo().info
    } catch (e: ForbiddenException) {
      throw PermissionException(e)
    }

  override fun realm(name: String): RealmClientImpl {
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

      return RealmClientImpl(kc, clientSettings, realmRes)

    } catch (e: NotFoundException) {
      throw StateException("Requested realm $name could not be fetched from Keycloak instance!", e)
    }
  }
}


