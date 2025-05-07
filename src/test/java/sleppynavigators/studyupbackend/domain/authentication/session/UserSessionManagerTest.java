package sleppynavigators.studyupbackend.domain.authentication.session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sleppynavigators.studyupbackend.common.IntegrationBaseTest;
import sleppynavigators.studyupbackend.domain.authentication.token.AccessToken;
import sleppynavigators.studyupbackend.domain.authentication.token.AccessTokenProperties;
import sleppynavigators.studyupbackend.domain.authentication.token.RefreshToken;
import sleppynavigators.studyupbackend.domain.user.User;
import sleppynavigators.studyupbackend.domain.user.vo.UserProfile;
import sleppynavigators.studyupbackend.exception.business.SessionExpiredException;
import sleppynavigators.studyupbackend.exception.network.InvalidCredentialException;

@DisplayName("[도메인] UserSessionManager 테스트")
class UserSessionManagerTest extends IntegrationBaseTest {

    @Autowired
    private UserSessionManager userSessionManager;

    @Autowired
    private AccessTokenProperties accessTokenProperties;

    @Test
    @DisplayName("세션 시작 - 성공")
    void startSession_Success() {
        // given
        User user = new User("test-user", "email@test.com");
        UserSession userSession = UserSession.builder().user(user).build();

        // when
        userSessionManager.startSession(userSession);

        // then
        assertThat(userSession.getRefreshToken()).isNotBlank();
        AccessToken issuedAccessToken = AccessToken.deserialize(userSession.getAccessToken(),
                accessTokenProperties);
        assertThat(issuedAccessToken.getUserProfile().getUsername()).isEqualTo("test-user");
        assertThat(userSession.isAlive()).isTrue();
    }

    @Test
    @DisplayName("세션 연장 - 성공")
    void extendSession_Success() {
        // given
        UserProfile userProfile = new UserProfile("test-user", "email@test.com");

        RefreshToken refreshToken = new RefreshToken();
        AccessToken accessToken = new AccessToken(1L, userProfile, List.of("profile"), accessTokenProperties);
        LocalDateTime notExpiredTime = LocalDateTime.now().plusMinutes(1);

        UserSession userSession = UserSession.builder()
                .user(new User("test-user", "email@test.com"))
                .refreshToken(refreshToken.serialize())
                .accessToken(accessToken.serialize(accessTokenProperties))
                .expiration(notExpiredTime)
                .build();

        // when
        userSessionManager.extendSession(userSession, refreshToken, accessToken);

        // then
        assertThat(userSession.getRefreshToken()).isNotBlank();
        AccessToken issuedAccessToken = AccessToken.deserialize(userSession.getAccessToken(),
                accessTokenProperties);
        assertThat(issuedAccessToken.getUserId()).isEqualTo(1L);
        assertThat(userSession.isAlive()).isTrue();
    }

    @Test
    @DisplayName("세션 연장 - 만료된 세션")
    void extendSession_ExpiredSession_Fail() {
        // given
        UserProfile userProfile = new UserProfile("test-user", "email@test.com");

        RefreshToken refreshToken = new RefreshToken();
        AccessToken accessToken = new AccessToken(1L, userProfile, List.of("profile"), accessTokenProperties);

        LocalDateTime expiredTime = LocalDateTime.now().minusMinutes(1);
        UserSession userSession = UserSession.builder()
                .user(new User("test-user", "email@test.com"))
                .refreshToken(refreshToken.serialize())
                .accessToken(accessToken.serialize(accessTokenProperties))
                .expiration(expiredTime)
                .build();

        // when & then
        assertThatThrownBy(() -> userSessionManager.extendSession(userSession, refreshToken, accessToken))
                .isInstanceOf(SessionExpiredException.class);
    }

    @Test
    @DisplayName("세션 연장 - 일치하지 않은 토큰")
    void extendSession_InvalidToken_Fail() {
        // given
        UserProfile userProfile = new UserProfile("test-user", "email@test.com");
        LocalDateTime notExpiredTime = LocalDateTime.now().plusMinutes(1);

        RefreshToken invalidRefreshToken = new RefreshToken();
        AccessToken invalidAccessToken = new AccessToken(1L, userProfile, List.of("profile"),
                accessTokenProperties);

        UserSession userSession = UserSession.builder()
                .user(new User("test-user", "email@test.com"))
                .refreshToken("refresh-token")
                .accessToken("access-token")
                .expiration(notExpiredTime)
                .build();

        // when & then
        assertThatThrownBy(
                () -> userSessionManager.extendSession(userSession, invalidRefreshToken,
                        invalidAccessToken))
                .isInstanceOf(InvalidCredentialException.class);
    }
}
