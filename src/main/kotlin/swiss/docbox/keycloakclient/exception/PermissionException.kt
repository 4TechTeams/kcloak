package swiss.docbox.keycloakclient.exception

class PermissionException(cause: Throwable) : KeycloakClientException("Permission denied", cause) {


}
