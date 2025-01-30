package sleppynavigators.studyupbackend.domain.authentication.session;

import sleppynavigators.studyupbackend.domain.authentication.UserCredential;
import sleppynavigators.studyupbackend.domain.authentication.token.AccessToken;
import sleppynavigators.studyupbackend.domain.authentication.token.RefreshToken;

/**
 * Manages user sessions.
 */
public interface SessionManager {

    /**
     * Start a new session for the given user.
     *
     * @param userSession    the session to start
     * @param userCredential the user to start a session for
     */
    void startSession(UserSession userSession, UserCredential userCredential);

    /**
     * Extends the expiration time of the given session.
     * <p>
     * If there is an inappropriate request, a failsafe such as resetting the session may be required.
     *
     * @param userSession  the session to extend
     * @param refreshToken the refresh token received
     * @param accessToken  the access token received
     */
    void extendSession(UserSession userSession, RefreshToken refreshToken, AccessToken accessToken);
}
