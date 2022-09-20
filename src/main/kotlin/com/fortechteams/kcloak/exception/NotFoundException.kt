package com.fortechteams.kcloak.exception

class NotFoundException(message: String, e: Throwable) : KCloakException(message, e) {
}
