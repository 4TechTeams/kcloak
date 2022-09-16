package com.fortechteams.kcloak.exception

class StateException(message: String, cause: Throwable? = null) : KeycloakClientException(message, cause) {
}
