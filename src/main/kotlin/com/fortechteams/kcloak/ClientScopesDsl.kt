package com.fortechteams.kcloak

import com.fortechteams.kcloak.exception.BadExpectationException
import com.fortechteams.kcloak.exception.PermissionException
import com.fortechteams.kcloak.exception.StateException
import org.keycloak.admin.client.resource.ClientScopesResource
import org.keycloak.representations.idm.ClientRepresentation
import org.keycloak.representations.idm.ClientScopeRepresentation
import javax.ws.rs.ForbiddenException
import javax.ws.rs.NotAllowedException

interface ClientScopesDsl {

  /**
   * Returns a list of all client scopes
   */
  fun all(): List<ClientScopeRepresentation>

  /**
   * Returns a [ClientScopeDsl] for a given client scope by its name
   *
   * If the client scope does not exist and [Settings.getCreateClientScopeIfNotExists] is not set to false, the client
   * scope will be created automatically
   */
  fun get(name: String): ClientScopeDsl

  /**
   * Creates a new client scope
   */
  fun create(name: String, updateFn: ClientScopeRepresentation.() -> Unit)
}

class ClientScopesDslImpl(
  private val clientScopesResource: ClientScopesResource
) : ClientScopesDsl {

  override fun get(name: String): ClientScopeDsl =
    try {

      val existingRep: ClientScopeRepresentation? = all().find { it.name == name }

      val rep: ClientScopeRepresentation =
        if (existingRep == null ) {
          all()
            .find { it.name == name }
            ?: throw StateException("Tried to create client-scope with name $name, but it still doesn't exist!")
        } else {
          existingRep
            ?: throw BadExpectationException("Client scope with name $name does not exist and auto-create has been disabled in settings.")
        }

      ClientScopeDslImpl(clientScopesResource.get(rep.id))

    } catch (e: ForbiddenException) {
      throw PermissionException(e)
    } catch (e: NotAllowedException) {
      throw PermissionException(e)
    }

  override fun all(): List<ClientScopeRepresentation> =
    try {
      clientScopesResource.findAll()
    } catch (e: ForbiddenException) {
      throw PermissionException(e)
    } catch (e: NotAllowedException) {
      throw PermissionException(e)
    }

  override fun create(name: String, updateFn: ClientScopeRepresentation.() -> Unit) {
    val cRep = ClientScopeRepresentation()
    cRep.name = name

    updateFn(cRep)

    try {
      clientScopesResource.create(cRep)
    } catch (e: ForbiddenException) {
      throw PermissionException(e)
    } catch (e: NotAllowedException) {
      throw PermissionException(e)
    }
  }
}
