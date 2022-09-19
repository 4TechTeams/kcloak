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
}
