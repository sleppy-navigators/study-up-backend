package sleppynavigators.studyupbackend.common.support;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sleppynavigators.studyupbackend.domain.authentication.session.UserSession;
import sleppynavigators.studyupbackend.domain.authentication.token.AccessToken;
import sleppynavigators.studyupbackend.domain.authentication.token.AccessTokenProperties;
import sleppynavigators.studyupbackend.domain.authentication.token.RefreshToken;
import sleppynavigators.studyupbackend.domain.user.User;
import sleppynavigators.studyupbackend.infrastructure.authentication.session.UserSessionRepository;

@Transactional
@Component
public class AuthSupport {

    @Autowired
    private AccessTokenProperties accessTokenProperties;

    @Autowired
    private UserSessionRepository userSessionRepository;

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

    public UserSession registerUserSession(User user, LocalDateTime expiration) {
        UserSession userSession = UserSession.builder()
                .user(user)
                .refreshToken(createRefreshToken())
                .accessToken(createAccessToken(user))
                .expiration(expiration)
                .build();
        return userSessionRepository.save(userSession);
    }
}
