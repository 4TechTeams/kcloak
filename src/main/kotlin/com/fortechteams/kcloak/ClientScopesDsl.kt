package com.fortechteams.kcloak

import com.fortechteams.kcloak.exception.StateException
import org.keycloak.admin.client.resource.ClientScopesResource
import org.keycloak.representations.idm.ClientScopeRepresentation

interface ClientScopesDsl {

  /**
   * Returns a list of all client scopes
   */
  fun all(): List<ClientScopeDsl>

  /**
   * Returns a list of all client scope representations
   */
  fun allRepresentations(): List<ClientScopeRepresentation>

  /**
   * Indicates if a client scope exists
   *
   * Note: This underlying call is [get], so if you anyway need the [ClientScopeDsl] / [ClientScopeRepresentation],
   * directly call [get] and add a null check, instead of calling [exists] and [get] successively.
   */
  fun exists(name: String): Boolean

  /**
   * Returns a [ClientScopeDsl] for a given client scope by its name
   */
  fun get(name: String): ClientScopeDsl?

  /**
   * Gets a client scope as its [ClientScopeRepresentation]
   */
  fun getRepresentation(name: String): ClientScopeRepresentation?

  /**
   * Creates a new client scope
   */
  fun create(name: String, updateFn: ClientScopeRepresentation.() -> Unit): ClientScopeDsl

  /**
   * Get a client as [ClientScopeDsl] or creates it, if it doesn't exist
   */
  fun getOrCreate(name: String): ClientScopeDsl

  /**
   * Gets & updates, or creates a client scope and returns it afterwards
   */
  fun ensure(name: String, updateFn: ClientScopeRepresentation.() -> Unit): ClientScopeDsl
}

class ClientScopesDslImpl(
  private val clientScopesResource: ClientScopesResource
) : ClientScopesDsl {

  private val errorHandler = ErrorHandler("client scope")

  override fun all(): List<ClientScopeDsl> =
    allRepresentations().map {
      ClientScopeDslImpl(clientScopesResource.get(it.id), it)
    }

  override fun allRepresentations(): List<ClientScopeRepresentation> =
    errorHandler.wrap {
      clientScopesResource.findAll()
    }

  override fun exists(name: String): Boolean =
    get(name) != null

  override fun get(name: String): ClientScopeDsl? =
    getRepresentation(name)
      ?.let { ClientScopeDslImpl(clientScopesResource.get(it.id)) }

  override fun getRepresentation(name: String): ClientScopeRepresentation? =
    errorHandler.wrap {
      allRepresentations()
        .find { it.name == name }
    }

  override fun create(name: String, updateFn: ClientScopeRepresentation.() -> Unit): ClientScopeDsl =
    errorHandler.wrap {
      val cRep = ClientScopeRepresentation()
      cRep.name = name

      updateFn(cRep)

      clientScopesResource.create(cRep)

      get(name)
        ?: throw StateException(
          "Client scope with id $name shoud have been created, but was not found. This is an internal error and either " +
            "indicates an implementation problem, or a race-condition."
        )
    }

  override fun getOrCreate(name: String): ClientScopeDsl =
    get(name) ?: create(name) {}

  override fun ensure(name: String, updateFn: ClientScopeRepresentation.() -> Unit): ClientScopeDsl =
    get(name)
      ?.also { it.update(updateFn) }
      ?: create(name, updateFn)
}
