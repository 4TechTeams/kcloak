package swiss.docbox.keycloakclient

import org.keycloak.OAuth2Constants
import org.keycloak.admin.client.KeycloakBuilder

object KCUtil {

    val localKeycloakBuilder by lazy {
        KeycloakBuilder.builder()
            .serverUrl("http://localhost:8090")
            .realm("master")
            .grantType(OAuth2Constants.PASSWORD)
            .clientId("admin-cli")
            .username("admin")
            .password("12345678")
    }
}