package sleppynavigators.studyupbackend.application.authentication;

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
import sleppynavigators.studyupbackend.infrastructure.authentication.UserCredentialRepository;
import sleppynavigators.studyupbackend.infrastructure.authentication.session.UserSessionRepository;
import sleppynavigators.studyupbackend.infrastructure.user.UserRepository;
import sleppynavigators.studyupbackend.presentation.authentication.dto.RefreshRequest;
import sleppynavigators.studyupbackend.presentation.authentication.dto.SignInRequest;
import sleppynavigators.studyupbackend.presentation.authentication.dto.TokenResponse;
import sleppynavigators.studyupbackend.presentation.authentication.exception.InvalidCredentialException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthService {

    private final UserCredentialRepository userCredentialRepository;
    private final UserRepository userRepository;
    private final UserSessionRepository userSessionRepository;
    private final SessionManager sessionManager;
    private final AccessTokenProperties accessTokenProperties;

    @Transactional
    public TokenResponse googleSignIn(SignInRequest request) {
        // TODO: implement real id-token verification
        //       for now, just fake it
        String subject = "fake-subject";

        UserCredential userCredential = userCredentialRepository.findBySubject(subject)
                .orElseGet(() -> {
                    // TODO: get user information from id-token
                    // TODO: extract user signup logic
                    UserProfile fakeProfile = new UserProfile("fake-name", "fake-email@test.com");
                    User user = userRepository.save(new User(fakeProfile));
                    return userCredentialRepository.save(new UserCredential(subject, "google", user));
                });

        User user = userCredential.getUser();
        UserSession userSession = userSessionRepository.findById(user.getId())
                .orElseGet(() -> userSessionRepository.save(new UserSession(user, null, null, null)));
        sessionManager.startSession(userSession);
        return new TokenResponse(userSession.getAccessToken(), userSession.getRefreshToken());
    }

    @Transactional
    public TokenResponse refresh(RefreshRequest request) {
        try {
            AccessToken accessToken = AccessToken.deserialize(request.accessToken(), accessTokenProperties);
            RefreshToken refreshToken = RefreshToken.deserialize(request.refreshToken());

            Long userId = accessToken.getUserId();
            UserSession userSession = userSessionRepository.findById(userId)
                    .orElseThrow(InvalidCredentialException::new);

            sessionManager.extendSession(userSession, refreshToken, accessToken);
            return new TokenResponse(userSession.getAccessToken(), userSession.getRefreshToken());
        } catch (JwtException ignored) {
            throw new InvalidCredentialException();
        }
    }
}
