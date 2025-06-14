package sleppynavigators.studyupbackend.common.support;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sleppynavigators.studyupbackend.application.authentication.AuthService;
import sleppynavigators.studyupbackend.domain.authentication.UserCredential;
import sleppynavigators.studyupbackend.domain.authentication.session.UserSession;
import sleppynavigators.studyupbackend.domain.authentication.token.AccessToken;
import sleppynavigators.studyupbackend.domain.authentication.token.AccessTokenProperties;
import sleppynavigators.studyupbackend.domain.authentication.token.RefreshToken;
import sleppynavigators.studyupbackend.domain.user.User;
import sleppynavigators.studyupbackend.infrastructure.authentication.UserCredentialRepository;
import sleppynavigators.studyupbackend.infrastructure.authentication.session.UserSessionRepository;
import sleppynavigators.studyupbackend.presentation.authentication.dto.request.SignInRequest;

@Transactional
@Component
public class AuthSupport {

    @Autowired
    private AccessTokenProperties accessTokenProperties;

    @Autowired
    private UserSessionRepository userSessionRepository;

    @Autowired
    private UserCredentialRepository userCredentialRepository;

    public String createBearerToken(User user) {
        return "Bearer " + createAccessToken(user);
    }

    public String createRefreshToken() {
        return new RefreshToken().serialize();
    }

    public String createAccessToken(User user) {
        List<String> authorities = List.of("profile");
        return new AccessToken(user.getId(), user.getUserProfile(), authorities, accessTokenProperties)
                .serialize(accessTokenProperties);
    }

    public String createExpiredAccessToken(User user) {
        List<String> authorities = List.of("profile");
        AccessTokenProperties expiredProperties =
                new AccessTokenProperties(accessTokenProperties.secret(), 100L);
        String willBeExpiredToken = new AccessToken(user.getId(), user.getUserProfile(), authorities, expiredProperties)
                .serialize(expiredProperties);

        // Wait for the token to be expired
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return willBeExpiredToken;
    }

    /**
     * <b>Caution!</b> This method do directly access the database. There's no consideration about side effects.
     *
     * @see AuthService#googleSignIn(SignInRequest)
     */
    public UserSession registerUserSessionToDB(User user, LocalDateTime expiration) {
        UserSession userSession = UserSession.builder()
                .user(user)
                .refreshToken(createRefreshToken())
                .accessToken(createExpiredAccessToken(user))
                .expiration(expiration)
                .build();
        return userSessionRepository.save(userSession);
    }

    /**
     * <b>Caution!</b> This method do directly access the database. There's no consideration about side effects.
     *
     * @see AuthService#googleSignIn(SignInRequest)
     */
    public UserCredential registerUserCredentialToDB() {
        User user = new User("test-user", "test-email");
        UserCredential userCredential = new UserCredential("test-subject", "provider", user);
        return userCredentialRepository.save(userCredential);
    }
}
