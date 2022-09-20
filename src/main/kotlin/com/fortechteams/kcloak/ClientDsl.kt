package com.fortechteams.kcloak

import org.keycloak.admin.client.resource.ClientResource
import org.keycloak.representations.idm.ClientRepresentation

interface ClientDsl {

  /**
   * Returns a representation for the client
   */
  fun representation(): ClientRepresentation

  /**
   * Updates a clients properties
   */
  fun update(updateFn: ClientRepresentation.() -> Unit)

  fun enable()

  fun disable()
}

class ClientDslImpl(
  private val clientRes: ClientResource,
  private val clientRep: ClientRepresentation? = null
) : ClientDsl {

  private val errorHandler = ErrorHandler("client")

  override fun representation(): ClientRepresentation =
    errorHandler.wrap {
      clientRep ?: clientRes.toRepresentation()
    }

  override fun update(updateFn: ClientRepresentation.() -> Unit) {
    val rep = representation()
    updateFn(rep)

    errorHandler.wrap {
      clientRes.update(rep)
    }
  }

  override fun enable() =
    update {
      isEnabled = true
    }

  override fun disable() =
    update {
      isEnabled = false
    }
}
