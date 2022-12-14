package com.fortechteams.kcloak

import com.fortechteams.kcloak.exception.StateException
import org.keycloak.admin.client.resource.RealmsResource
import org.keycloak.representations.idm.RealmRepresentation
import org.slf4j.LoggerFactory
import javax.ws.rs.NotFoundException

interface RealmsDsl {

  /**
   * Returns a list of all realms as their respective [RealmDsl] implementation
   *
   * This is rarely useful, because in most cases, one needs a list of [RealmRepresentation], not full DSL objects. See
   * [allRepresentations] for that.
   */
  fun all(): List<RealmDsl>

  /**
   * Returns a list of all realm representations
   */
  fun allRepresentations(): List<RealmRepresentation>

  /**
   * Indicates if a realm exists
   *
   * Note: This underlying call is [get], so if you anyway need the [RealmDsl] / [RealmRepresentation], directly call
   * [get] and add a null check, instead of calling [exists] and [get] successively.
   */
  fun exists(name: String): Boolean

  /**
   * Gets a realm as its DSL
   */
  fun get(name: String): RealmDsl?

  /**
   * Gets a realm as its [RealmRepresentation]
   */
  fun getRepresentation(name: String): RealmRepresentation?

  /**
   * Creates a new realm
   *
   * Note: There is no "exists" check. You need to make sure a realm with a given name does not exist, before you create
   * one. Otherwise, this call will fail.
   */
  fun create(name: String, updateFn: RealmRepresentation.() -> Unit): RealmDsl

  /**
   * Get a realm as [RealmDsl] or creates it, if it doesn't exist
   *
   * Note: Newly created realms using this method are always disabled by default. use explicit [create] and supply
   * parameters accordingly, to directly enable realms. Otherwise, call [RealmDsl.enable] after creating.
   */
  fun getOrCreate(name: String): RealmDsl

  /**
   * Creates or updates a realm and returns it afterwards
   */
  fun ensure(name: String, updateFn: RealmRepresentation.() -> Unit): RealmDsl
}

class RealmsDslImpl(
  private val realmsResource: RealmsResource
) : RealmsDsl {

  private val log = LoggerFactory.getLogger(javaClass)
  private val errorHandler = ErrorHandler("realm")

  override fun all(): List<RealmDsl> =
    allRepresentations()
      .map {
        RealmDslImpl(realmsResource.realm(it.realm), it)
      }

  override fun allRepresentations(): List<RealmRepresentation> =
    errorHandler.wrap {
      realmsResource.findAll()
    }

  override fun exists(name: String): Boolean =
    get(name) != null

  override fun get(name: String): RealmDsl? =
    errorHandler.wrap {
      try {
        // realmsResource.realm does not validate if the realm exists, therefore we need this workaround to check...
        realmsResource
          .findAll()
          .find { it.realm == name }
          ?.let {
            RealmDslImpl(realmsResource.realm(it.realm))
          }
      } catch (e: NotFoundException) {
        // we're a bit less strict here and support null instead of throwing exceptions
        null
      }
    }

  override fun getRepresentation(name: String): RealmRepresentation? =
    errorHandler.wrap {
      try {
        realmsResource.realm(name).toRepresentation()
      } catch (e: NotFoundException) {
        // we're a bit less strict here and support null instead of throwing exceptions
        null
      }
    }

  override fun create(name: String, updateFn: RealmRepresentation.() -> Unit): RealmDsl =
    errorHandler.wrap {
      log.debug("Creating realm with name $name")
      val rep = RealmRepresentation()
      rep.realm = name

      updateFn(rep)

      realmsResource.create(rep)

      get(name) ?: throw StateException(
        "Realm $name shoud have been created, but was not found. This is an internal error and either indicates an " +
          "implementation problem, or a race-condition."
      )
    }

  override fun getOrCreate(name: String): RealmDsl =
    get(name) ?: create(name) {
      isEnabled = false
    }

  override fun ensure(name: String, updateFn: RealmRepresentation.() -> Unit): RealmDsl =
    get(name)
      ?.also { it.update(updateFn) }
      ?: create(name, updateFn)
}
