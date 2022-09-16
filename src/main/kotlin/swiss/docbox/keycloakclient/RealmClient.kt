package swiss.docbox.keycloakclient

import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.resource.RealmResource
import org.keycloak.representations.idm.RealmRepresentation
import swiss.docbox.keycloakclient.exception.PermissionException
import javax.ws.rs.ForbiddenException
import javax.ws.rs.NotAllowedException

class RealmClient(
    private val kc: Keycloak,
    private val clientSettings: ClientSettings,
    private val realmRes: RealmResource
) {

    fun representation(): RealmRepresentation =
        realmRes.toRepresentation()

    fun updateSettings(updateFn: RealmRepresentation.() -> Unit) {
        val rep = representation()
        updateFn(rep)

        try {
            realmRes.update(rep)
        } catch (e: ForbiddenException) {
            throw PermissionException(e)
        } catch (e: NotAllowedException) {
            throw PermissionException(e)
        }
    }


}
