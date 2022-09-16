package com.fortechteams.kcloak.exception

class PermissionException(cause: Throwable) : KCloakException("Permission denied", cause) {


}
