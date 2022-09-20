package com.fortechteams.kcloak

/**
 * Provides behavioral settings for a given KCloak instance
 */
class Settings {

  /**
   * Indicates if a missing realm should be created automatically or not
   *
   * Auto-created realms are always disabled by default
   */
  var createRealmIfNotExists: Boolean = true

  /**
   * Indicates if a missing client should be created automatically or not
   *
   * Auto-created clients are always disabled by default
   */
  var createClientIfNotExists: Boolean = true

  /**
   * Indicates if a missing client scope should be created automatically or not
   *
   * Auto-created clients scopes are always disabled by default
   */
  var createClientScopeIfNotExists: Boolean = true
}
