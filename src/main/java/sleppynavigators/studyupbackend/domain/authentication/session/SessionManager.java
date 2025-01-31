package sleppynavigators.studyupbackend.domain.authentication.session;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sleppynavigators.studyupbackend.domain.authentication.UserCredential;
import sleppynavigators.studyupbackend.domain.authentication.token.AccessToken;
import sleppynavigators.studyupbackend.domain.authentication.token.AccessTokenProperties;
import sleppynavigators.studyupbackend.domain.authentication.token.RefreshToken;
import sleppynavigators.studyupbackend.domain.authentication.token.RefreshTokenProperties;
import sleppynavigators.studyupbackend.domain.user.User;
import sleppynavigators.studyupbackend.domain.user.vo.UserProfile;
import sleppynavigators.studyupbackend.presentation.authentication.exception.InvalidCredentialException;
import sleppynavigators.studyupbackend.presentation.authentication.exception.SessionExpiredException;

/**
 * Manages user sessions.
 */

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class SessionManager {

    private final RefreshTokenProperties refreshTokenProperties;
    private final AccessTokenProperties accessTokenProperties;

    public void startSession(UserSession userSession, UserCredential userCredential) {
        validateStartRequest(userSession, userCredential);

        RefreshToken refreshToken = new RefreshToken();
        AccessToken accessToken = createAccessToken(userCredential, accessTokenProperties);

        userSession.update(refreshToken.serialize(),
                accessToken.serialize(accessTokenProperties),
                LocalDateTime.now().plusMinutes(refreshTokenProperties.expirationInMinutes()));
    }

    public void extendSession(UserSession userSession, RefreshToken refreshToken, AccessToken accessToken) {
        String serializedRefreshToken = refreshToken.serialize();
        String serializedAccessToken = accessToken.serialize(accessTokenProperties);

        // TODO: implement a failsafe mechanism
        validateExtendRequest(userSession, serializedRefreshToken, serializedAccessToken);

        String newRefreshToken = refreshToken.rotate().serialize();
        String newAccessToken = accessToken.rotate(accessTokenProperties).serialize(accessTokenProperties);
        LocalDateTime newExpiration = LocalDateTime.now().plusMinutes(refreshTokenProperties.expirationInMinutes());

        userSession.update(newRefreshToken, newAccessToken, newExpiration);
    }

    private void validateStartRequest(UserSession userSession, UserCredential userCredential) {
        if (!userSession.getUser().equals(userCredential.getUser())) {
            throw new InvalidCredentialException();
        }
    }

    private AccessToken createAccessToken(UserCredential userCredential, AccessTokenProperties accessTokenProperties) {
        User user = userCredential.getUser();
        Long userId = user.getId();
        UserProfile userProfile = user.getUserProfile();
        List<String> authorities = List.of("profile"); // TODO: requires specification
        return new AccessToken(userId, userProfile, authorities, accessTokenProperties);
    }

    private void validateExtendRequest(UserSession userSession, String refreshToken, String accessToken) {
        if (!userSession.isValidToken(refreshToken, accessToken)) {
            throw new InvalidCredentialException();
        }

        if (!userSession.isAlive()) {
            throw new SessionExpiredException();
        }
    }
}
