package com.fortechteams.kcloak

import com.fortechteams.kcloak.builder.ProtocolMapperBuilder
import com.fortechteams.kcloak.exception.StateException
import org.keycloak.admin.client.resource.ProtocolMappersResource
import org.keycloak.representations.idm.ClientScopeRepresentation
import org.keycloak.representations.idm.ProtocolMapperRepresentation

interface ProtocolMappersDsl {

  /**
   * Returns a list of all protocol mappers
   */
  fun all(): List<ProtocolMapperDsl>

  /**
   * Returns a list of all protocol mapper representations
   */
  fun allRepresentations(): List<ProtocolMapperRepresentation>

  /**
   * Indicates if a protocol mapper exists
   *
   * Note: This underlying call is [get], so if you anyway need the [ProtocolMapperDsl] / [ProtocolMapperRepresentation],
   * directly call [get] and add a null check, instead of calling [exists] and [get] successively.
   */
  fun exists(name: String): Boolean

  /**
   * Returns a [ProtocolMapperDsl] for a given protocol mapper by its name
   */
  fun get(name: String): ProtocolMapperDsl?

  /**
   * Gets a protocol mapper as its [ClientScopeRepresentation]
   */
  fun getRepresentation(name: String): ProtocolMapperRepresentation?

  /**
   * Creates a new protocol mapper
   */
  fun create(name: String, updateFn: ProtocolMapperRepresentation.() -> Unit): ProtocolMapperDsl
  fun create(name: String, builder: ProtocolMapperBuilder): ProtocolMapperDsl

  /**
   * Get a client as [ProtocolMapperDsl] or creates it, if it doesn't exist
   */
  fun getOrCreate(name: String): ProtocolMapperDsl

  /**
   * Gets & updates, or creates a protocol mapper and returns it afterwards
   */
  fun ensure(name: String, updateFn: ProtocolMapperRepresentation.() -> Unit): ProtocolMapperDsl
  fun ensure(name: String, builder: ProtocolMapperBuilder): ProtocolMapperDsl
}

class ProtocolMappersDslImpl(
  private val protocolMappersRes: ProtocolMappersResource
) : ProtocolMappersDsl {

  private val errorHandler = ErrorHandler("protocol mapper")

  override fun all(): List<ProtocolMapperDsl> =
    allRepresentations()
      .map { ProtocolMapperDslImpl(protocolMappersRes, protocolMappersRes.getMapperById(it.id)) }

  override fun allRepresentations(): List<ProtocolMapperRepresentation> =
    errorHandler.wrap {
      protocolMappersRes.mappers
    }

  override fun exists(name: String): Boolean =
    get(name) != null

  override fun get(name: String): ProtocolMapperDsl? =
    getRepresentation(name)
      ?.let { ProtocolMapperDslImpl(protocolMappersRes, it) }

  override fun getRepresentation(name: String): ProtocolMapperRepresentation? =
    allRepresentations()
      .find { it.name == name }

  override fun create(name: String, updateFn: ProtocolMapperRepresentation.() -> Unit): ProtocolMapperDsl =
    errorHandler.wrap {
      if (exists(name)) {
        throw StateException("Protocol mapper with name $name already exists.")
      }

      val rep = ProtocolMapperRepresentation()
      rep.name = name
      updateFn(rep)

      protocolMappersRes.createMapper(rep)

      get(name)
        ?: throw StateException(
          "Protocol mapper with name $name should have been created, but was not found. This is an internal error and " +
            "either indicates an implementation problem, or a race-condition."
        )
    }

  override fun create(name: String, builder: ProtocolMapperBuilder): ProtocolMapperDsl =
    create(name) {
      builder.modify(this)
    }

  override fun getOrCreate(name: String): ProtocolMapperDsl =
    get(name) ?: create(name) {}

  override fun ensure(name: String, updateFn: ProtocolMapperRepresentation.() -> Unit): ProtocolMapperDsl =
    get(name)
      ?.also { it.update(updateFn) }
      ?: create(name, updateFn)

  override fun ensure(name: String, builder: ProtocolMapperBuilder): ProtocolMapperDsl =
    ensure(name) {
      builder.modify(this)
    }
}
