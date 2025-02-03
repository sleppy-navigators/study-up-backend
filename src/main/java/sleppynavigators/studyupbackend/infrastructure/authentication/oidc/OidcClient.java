package sleppynavigators.studyupbackend.infrastructure.authentication.oidc;

import io.jsonwebtoken.Claims;

/**
 * OIDC client for 3rd party authentication
 */
public interface OidcClient {

    /**
     * Parse and verify the idToken
     *
     * @param idToken idToken from 3rd party authentication
     * @return id-token claims
     */
    Claims deserialize(String idToken);
}
