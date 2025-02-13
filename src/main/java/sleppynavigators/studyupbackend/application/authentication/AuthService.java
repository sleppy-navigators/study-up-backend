package sleppynavigators.studyupbackend.application.authentication;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sleppynavigators.studyupbackend.domain.authentication.UserCredential;
import sleppynavigators.studyupbackend.domain.authentication.session.SessionManager;
import sleppynavigators.studyupbackend.domain.authentication.session.UserSession;
import sleppynavigators.studyupbackend.domain.authentication.token.AccessToken;
import sleppynavigators.studyupbackend.domain.authentication.token.AccessTokenProperties;
import sleppynavigators.studyupbackend.domain.authentication.token.RefreshToken;
import sleppynavigators.studyupbackend.domain.user.User;
import sleppynavigators.studyupbackend.domain.user.vo.UserProfile;
import sleppynavigators.studyupbackend.exception.database.EntityNotFoundException;
import sleppynavigators.studyupbackend.infrastructure.authentication.UserCredentialRepository;
import sleppynavigators.studyupbackend.infrastructure.authentication.oidc.GoogleOidcClient;
import sleppynavigators.studyupbackend.infrastructure.authentication.session.UserSessionRepository;
import sleppynavigators.studyupbackend.infrastructure.user.UserRepository;
import sleppynavigators.studyupbackend.presentation.authentication.dto.RefreshRequest;
import sleppynavigators.studyupbackend.presentation.authentication.dto.SignInRequest;
import sleppynavigators.studyupbackend.presentation.authentication.dto.TokenResponse;
import sleppynavigators.studyupbackend.exception.network.InvalidCredentialException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthService {

    private final UserCredentialRepository userCredentialRepository;
    private final UserRepository userRepository;
    private final UserSessionRepository userSessionRepository;
    private final SessionManager sessionManager;
    private final AccessTokenProperties accessTokenProperties;
    private final GoogleOidcClient googleOidcClient;

    @Transactional
    public TokenResponse googleSignIn(SignInRequest request) {
        Claims idTokenClaims = googleOidcClient.deserialize(request.idToken());
        String subject = idTokenClaims.getSubject();
        String username = idTokenClaims.get("name", String.class);
        String email = idTokenClaims.get("email", String.class);
        return signIn(subject, new UserProfile(username, email), "google");
    }

    @Transactional
    public TokenResponse refresh(RefreshRequest request) {
        try {
            AccessToken accessToken = AccessToken.deserialize(request.accessToken(), accessTokenProperties);
            RefreshToken refreshToken = RefreshToken.deserialize(request.refreshToken());

            Long userId = accessToken.getUserId();
            UserSession userSession = userSessionRepository.findByUserId(userId)
                    .orElseThrow(EntityNotFoundException::new);

            sessionManager.extendSession(userSession, refreshToken, accessToken);
            return new TokenResponse(userSession.getAccessToken(), userSession.getRefreshToken());
        } catch (JwtException ignored) {
            throw new InvalidCredentialException();
        }
    }

    private TokenResponse signIn(String subject, UserProfile userProfile, String provider) {
        UserCredential userCredential = userCredentialRepository
                .findBySubject(subject)
                .orElseGet(() -> { // sign up
                    User user = userRepository.save(new User(userProfile));
                    return userCredentialRepository.save(new UserCredential(subject, provider, user));
                });

        User user = userCredential.getUser();
        UserSession userSession = userSessionRepository.findByUserId(user.getId())
                .orElseGet(() -> userSessionRepository.save(new UserSession(user, null, null, null)));
        sessionManager.startSession(userSession);
        return new TokenResponse(userSession.getAccessToken(), userSession.getRefreshToken());
    }
}
