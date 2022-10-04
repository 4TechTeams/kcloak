package com.fortechteams.kcloak

import com.fortechteams.kcloak.exception.StateException
import org.keycloak.admin.client.resource.ClientsResource
import org.keycloak.representations.idm.ClientRepresentation
import javax.ws.rs.NotFoundException

interface ClientsDsl {

  /**
   * Returns a list of all clients on the given realm
   */
  fun all(): List<ClientDsl>

  /**
   * Returns a list of all client representations
   */
  fun allRepresentations(): List<ClientRepresentation>

  /**
   * Indicates if a client exists
   *
   * Note: This underlying call is [get], so if you anyway need the [ClientDsl] / [ClientRepresentation], directly call
   * [get] and add a null check, instead of calling [exists] and [get] successively.
   */
  fun exists(name: String): Boolean

  /**
   * Gets a client as its DSL
   */
  fun get(name: String): ClientDsl?

  /**
   * Gets a client as its [ClientRepresentation]
   */
  fun getRepresentation(name: String): ClientRepresentation?

  /**
   * Creates a new realm
   *
   * Note: There is no "exists" check. You need to make sure an entity with a given name does not exist, before you
   * create one. Otherwise, this call will fail.
   */
  fun create(name: String, updateFn: ClientRepresentation.() -> Unit): ClientDsl

  /**
   * Get a client as [ClientDsl] or creates it, if it doesn't exist
   *
   * Note: Newly created clients using this method are always disabled by default. use explicit [create] and supply
   * parameters accordingly, to directly enable clients. Otherwise, call [ClientDsl.enable] after creating.
   */
  fun getOrCreate(name: String): ClientDsl

  /**
   * Gets & updates, or creates a client and returns it afterwards
   */
  fun ensure(name: String, updateFn: ClientRepresentation.() -> Unit): ClientDsl
}

class ClientsDslImpl(
  private val clientsResource: ClientsResource
) : ClientsDsl {

  private val errorHandler = ErrorHandler("client")

  override fun all(): List<ClientDsl> =
    allRepresentations()
      .map {
        ClientDslImpl(clientsResource.get(it.id), it)
      }

  override fun allRepresentations(): List<ClientRepresentation> =
    errorHandler.wrap {
      clientsResource.findAll()
    }

  override fun exists(name: String): Boolean =
    getRepresentation(name) != null

  override fun get(name: String): ClientDsl? =
    errorHandler.wrap {
      getRepresentation(name)
        ?.let {
          ClientDslImpl(clientsResource.get(it.id), it)
        }
    }

  override fun getRepresentation(name: String): ClientRepresentation? =
    errorHandler.wrap {
      try {
        clientsResource.findByClientId(name)?.firstOrNull()
      } catch (e: NotFoundException) {
        null
      }
    }

  override fun create(name: String, updateFn: ClientRepresentation.() -> Unit): ClientDsl =
    errorHandler.wrap {
      val rep = ClientRepresentation()
      rep.clientId = name

      updateFn(rep)

      clientsResource.create(rep)

      get(name) ?: throw StateException(
        "Client with id $name shoud have been created, but was not found. This is an internal error and either " +
          "indicates an implementation problem, or a race-condition."
      )
    }

  override fun getOrCreate(name: String): ClientDsl =
    get(name) ?: create(name) {
      isEnabled = false
    }

  override fun ensure(name: String, updateFn: ClientRepresentation.() -> Unit): ClientDsl =
    get(name)
      ?.also { it.update(updateFn) }
      ?: create(name, updateFn)
}
