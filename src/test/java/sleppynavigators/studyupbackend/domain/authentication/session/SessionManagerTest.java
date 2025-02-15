package sleppynavigators.studyupbackend.domain.authentication.session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import sleppynavigators.studyupbackend.domain.authentication.token.AccessToken;
import sleppynavigators.studyupbackend.domain.authentication.token.AccessTokenProperties;
import sleppynavigators.studyupbackend.domain.authentication.token.RefreshToken;
import sleppynavigators.studyupbackend.domain.user.User;
import sleppynavigators.studyupbackend.domain.user.vo.UserProfile;
import sleppynavigators.studyupbackend.exception.network.InvalidCredentialException;
import sleppynavigators.studyupbackend.exception.business.SessionExpiredException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("Session Manager 테스트")
class SessionManagerTest {

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private AccessTokenProperties accessTokenProperties;

    @Test
    @DisplayName("세션을 시작한다")
    void whenStartSession_Success() {
        // given
        User user = new User("test-user", "email@test.com");
        UserSession userSession = UserSession.builder().user(user).build();

        // when
        sessionManager.startSession(userSession);

        // then
        assertThat(userSession.getRefreshToken()).isNotBlank();
        AccessToken issuedAccessToken = AccessToken.deserialize(userSession.getAccessToken(), accessTokenProperties);
        assertThat(issuedAccessToken.getUserProfile().username()).isEqualTo("test-user");
        assertThat(userSession.isAlive()).isTrue();
    }

    @Test
    @DisplayName("세션을 연장한다")
    void whenExtendSession_Success() {
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
        sessionManager.extendSession(userSession, refreshToken, accessToken);

        // then
        assertThat(userSession.getRefreshToken()).isNotBlank();
        AccessToken issuedAccessToken = AccessToken.deserialize(userSession.getAccessToken(), accessTokenProperties);
        assertThat(issuedAccessToken.getUserId()).isEqualTo(1L);
        assertThat(userSession.isAlive()).isTrue();
    }

    @Test
    @DisplayName("세션 연장 요청이 만료된 세션에 대해 실패한다")
    void whenExtendSession_ExpiredSession_thenFail() {
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
        assertThatThrownBy(() -> sessionManager.extendSession(userSession, refreshToken, accessToken))
                .isInstanceOf(SessionExpiredException.class);
    }

    @Test
    @DisplayName("세션 연장 요청이 일치하지 않은 토큰에 대해 실패한다")
    void whenExtendSession_InvalidToken_thenFail() {
        // given
        UserProfile userProfile = new UserProfile("test-user", "email@test.com");
        LocalDateTime notExpiredTime = LocalDateTime.now().plusMinutes(1);

        RefreshToken invalidRefreshToken = new RefreshToken();
        AccessToken invalidAccessToken =
                new AccessToken(1L, userProfile, List.of("profile"), accessTokenProperties);

        UserSession userSession = UserSession.builder()
                .user(new User("test-user", "email@test.com"))
                .refreshToken("refresh-token")
                .accessToken("access-token")
                .expiration(notExpiredTime)
                .build();

        // when & then
        assertThatThrownBy(
                () -> sessionManager.extendSession(userSession, invalidRefreshToken, invalidAccessToken))
                .isInstanceOf(InvalidCredentialException.class);
    }
}
