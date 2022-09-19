package com.fortechteams.kcloak

import com.fortechteams.kcloak.exception.BadExpectationException
import com.fortechteams.kcloak.exception.PermissionException
import org.keycloak.admin.client.resource.ClientsResource
import org.keycloak.representations.idm.ClientRepresentation
import javax.ws.rs.ForbiddenException
import javax.ws.rs.NotAllowedException

interface ClientsDsl {

  /**
   * Alias for [ClientsDsl.get]
   */
  operator fun invoke(clientId: String): ClientDsl = get(clientId)

  /**
   * Returns a list of all clients on the given realm
   */
  fun all(): List<ClientRepresentation>

  /**
   * Gets the [ClientRepresentation] for a given client id
   *
   * If the client does not exist and the setting [Settings.getCreateClientIfNotExists] is not set to false, it will be
   * created automatically.
   */
  fun get(clientId: String): ClientDsl

  /**
   * Creates a new client with given clientId and settings
   */
  fun create(clientId: String, updateFn: ClientRepresentation.() -> Unit)
}

class ClientsDslImpl(
  private val settings: Settings,
  private val clientsResource: ClientsResource
) : ClientsDsl {

  override fun all(): List<ClientRepresentation> =
    try {
      clientsResource.findAll()
    } catch (e: ForbiddenException) {
      throw PermissionException(e)
    } catch (e: NotAllowedException) {
      throw PermissionException(e)
    }

  override fun get(clientId: String): ClientDsl =
    try {
      val existing: ClientRepresentation? = clientsResource.findByClientId(clientId)?.firstOrNull()

      val cRep = if (existing == null && settings.createClientIfNotExists) {
        create(clientId) {
          isEnabled = false
        }
        clientsResource.findByClientId(clientId).first()

      } else {
        existing
          ?: throw BadExpectationException("Client with id $clientId does not exist and auto-creation has been disabled")
      }

      ClientDslImpl(settings, clientsResource.get(cRep.id))

    } catch (e: ForbiddenException) {
      throw PermissionException(e)
    } catch (e: NotAllowedException) {
      throw PermissionException(e)
    }

  override fun create(clientId: String, updateFn: ClientRepresentation.() -> Unit) {

    val cRep = ClientRepresentation()
    cRep.clientId = clientId

    updateFn(cRep)

    try {
      clientsResource.create(cRep)
    } catch (e: ForbiddenException) {
      throw PermissionException(e)
    } catch (e: NotAllowedException) {
      throw PermissionException(e)
    }
  }


}
