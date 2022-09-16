package com.fortechteams.kcloak.exception

class PermissionException(cause: Throwable) : KeycloakClientException("Permission denied", cause) {


}
