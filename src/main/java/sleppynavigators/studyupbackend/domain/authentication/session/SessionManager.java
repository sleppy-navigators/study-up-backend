package sleppynavigators.studyupbackend.domain.authentication.session;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sleppynavigators.studyupbackend.domain.authentication.token.AccessToken;
import sleppynavigators.studyupbackend.domain.authentication.token.AccessTokenProperties;
import sleppynavigators.studyupbackend.domain.authentication.token.RefreshToken;
import sleppynavigators.studyupbackend.domain.authentication.token.RefreshTokenProperties;
import sleppynavigators.studyupbackend.domain.user.User;
import sleppynavigators.studyupbackend.exception.request.InvalidCredentialException;
import sleppynavigators.studyupbackend.exception.business.SessionExpiredException;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class SessionManager {

    private final RefreshTokenProperties refreshTokenProperties;
    private final AccessTokenProperties accessTokenProperties;

    public void startSession(UserSession userSession) {
        RefreshToken refreshToken = new RefreshToken();
        AccessToken accessToken = issueAccessToken(userSession.getUser(), accessTokenProperties);

        userSession.update(refreshToken.serialize(),
                accessToken.serialize(accessTokenProperties),
                LocalDateTime.now().plusMinutes(refreshTokenProperties.expirationInMinutes()));
    }

    public void extendSession(UserSession userSession, RefreshToken refreshToken, AccessToken accessToken) {
        validateExtendRequest(userSession, refreshToken, accessToken);

        RefreshToken newRefreshToken = refreshToken.rotate();
        AccessToken newAccessToken = accessToken.rotate(accessTokenProperties);
        LocalDateTime newExpiration = LocalDateTime.now().plusMinutes(refreshTokenProperties.expirationInMinutes());

        userSession.update(newRefreshToken.serialize(), newAccessToken.serialize(accessTokenProperties), newExpiration);
    }

    private AccessToken issueAccessToken(User user, AccessTokenProperties accessTokenProperties) {
        List<String> authorities = List.of("profile"); // TODO: requires specification
        return new AccessToken(user.getId(), user.getUserProfile(), authorities, accessTokenProperties);
    }

    private void validateExtendRequest(UserSession userSession, RefreshToken refreshToken, AccessToken accessToken) {
        if (!userSession.isRegistered(refreshToken.serialize(), accessToken.serialize(accessTokenProperties))) {
            // TODO: implement a failsafe mechanism
            throw new InvalidCredentialException();
        }

        if (!userSession.isAlive()) {
            throw new SessionExpiredException();
        }
    }
}
