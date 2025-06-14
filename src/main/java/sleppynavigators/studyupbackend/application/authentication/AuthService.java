package sleppynavigators.studyupbackend.application.authentication;

import io.jsonwebtoken.Claims;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sleppynavigators.studyupbackend.domain.authentication.UserCredential;
import sleppynavigators.studyupbackend.domain.authentication.session.UserSession;
import sleppynavigators.studyupbackend.domain.authentication.session.UserSessionManager;
import sleppynavigators.studyupbackend.domain.authentication.token.AccessToken;
import sleppynavigators.studyupbackend.domain.authentication.token.AccessTokenProperties;
import sleppynavigators.studyupbackend.domain.authentication.token.RefreshToken;
import sleppynavigators.studyupbackend.domain.user.User;
import sleppynavigators.studyupbackend.exception.database.EntityNotFoundException;
import sleppynavigators.studyupbackend.infrastructure.authentication.UserCredentialRepository;
import sleppynavigators.studyupbackend.infrastructure.authentication.oidc.GoogleOidcClient;
import sleppynavigators.studyupbackend.infrastructure.authentication.session.UserSessionRepository;
import sleppynavigators.studyupbackend.presentation.authentication.dto.request.RefreshRequest;
import sleppynavigators.studyupbackend.presentation.authentication.dto.request.SignInRequest;
import sleppynavigators.studyupbackend.presentation.authentication.dto.response.TokenResponse;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthService {

    private final UserCredentialRepository userCredentialRepository;
    private final UserSessionRepository userSessionRepository;
    private final UserSessionManager userSessionManager;
    private final AccessTokenProperties accessTokenProperties;
    private final GoogleOidcClient googleOidcClient;

    @Transactional
    public TokenResponse googleSignIn(SignInRequest request) {
        Claims idTokenClaims = googleOidcClient.deserialize(request.idToken());

        String subject = idTokenClaims.getSubject();
        String username = idTokenClaims.get("name", String.class);
        String email = idTokenClaims.get("email", String.class);

        return signIn(subject, username, email, "google");
    }

    @Transactional
    public TokenResponse refresh(RefreshRequest request) {
        AccessToken accessToken = AccessToken
                .deserializeEvenIfExpired(request.accessToken(), accessTokenProperties);
        RefreshToken refreshToken = RefreshToken.deserialize(request.refreshToken());

        Long userId = accessToken.getUserId();
        UserSession userSession = userSessionRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("User session not found - userId: " + userId));

        userSessionManager.extendSession(userSession, refreshToken, accessToken);
        return new TokenResponse(userSession.getAccessToken(), userSession.getRefreshToken());
    }

    private TokenResponse signIn(String subject, String username, String email, String provider) {
        UserCredential userCredential = userCredentialRepository.findBySubject(subject)
                .orElseGet(() -> signUp(subject, username, email, provider));

        User user = userCredential.getUser();
        UserSession userSession = userSessionRepository.findByUserId(user.getId())
                .orElseGet(() -> createSession(user));

        userSessionManager.startSession(userSession);
        return new TokenResponse(userSession.getAccessToken(), userSession.getRefreshToken());
    }

    private UserCredential signUp(String subject, String username, String email, String provider) {
        return userCredentialRepository.save(new UserCredential(subject, provider, new User(username, email)));
    }

    private UserSession createSession(User user) {
        UserSession userSession = UserSession.builder().user(user).build();
        return userSessionRepository.save(userSession);
    }
}
