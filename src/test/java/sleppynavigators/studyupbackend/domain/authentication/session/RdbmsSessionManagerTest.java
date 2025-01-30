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
import sleppynavigators.studyupbackend.domain.authentication.UserCredential;
import sleppynavigators.studyupbackend.domain.authentication.token.AccessToken;
import sleppynavigators.studyupbackend.domain.authentication.token.AccessTokenProperties;
import sleppynavigators.studyupbackend.domain.authentication.token.RefreshToken;
import sleppynavigators.studyupbackend.domain.user.User;
import sleppynavigators.studyupbackend.domain.user.vo.UserProfile;
import sleppynavigators.studyupbackend.presentation.authentication.exception.InvalidCredentialException;
import sleppynavigators.studyupbackend.presentation.authentication.exception.SessionExpiredException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("RDBMS 기반 Session Manager 테스트")
class RdbmsSessionManagerTest {

    @Autowired
    private RdbmsSessionManager rdbmsSessionManager;

    @Autowired
    private AccessTokenProperties accessTokenProperties;

    @Test
    @DisplayName("세션을 시작한다")
    void whenStartSession_Success() {
        // given
        UserProfile userProfile = new UserProfile("test-user", "email@test.com");
        User user = new User(userProfile);
        UserSession userSession = new UserSession(user, null, null, null);
        UserCredential userCredential = new UserCredential("test-sub", "provider", user);

        // when
        rdbmsSessionManager.startSession(userSession, userCredential);

        // then
        assertThat(userSession.getRefreshToken()).isNotBlank();
        AccessToken issuedAccessToken = AccessToken.deserialize(userSession.getAccessToken(), accessTokenProperties);
        assertThat(issuedAccessToken.getUserProfile().username()).isEqualTo("test-user");
        assertThat(userSession.isExpired()).isFalse();
    }

    @Test
    @DisplayName("유저 정보가 맞지 않으면 세션 시작에 실패한다")
    void whenStartSession_InvalidUser_thenFail() {
        // given
        UserProfile userProfile = new UserProfile("test-user", "email@test.com");
        User user = new User(userProfile);
        UserSession userSession = new UserSession(user, null, null, null);

        User invalidUser = new User(userProfile);
        UserCredential userCredential = new UserCredential("test-sub", "provider", invalidUser);

        // when & then
        assertThatThrownBy(() -> rdbmsSessionManager.startSession(userSession, userCredential))
                .isInstanceOf(InvalidCredentialException.class);
    }

    @Test
    @DisplayName("세션을 연장한다")
    void whenExtendSession_Success() {
        // given
        UserProfile userProfile = new UserProfile("test-user", "email@test.com");
        User user = new User(userProfile);
        LocalDateTime notExpiredTime = LocalDateTime.now().plusMinutes(1);
        RefreshToken refreshToken = new RefreshToken();
        AccessToken accessToken = new AccessToken(1L, userProfile, List.of("profile"), accessTokenProperties);
        UserSession userSession = new UserSession(user,
                refreshToken.serialize(), accessToken.serialize(accessTokenProperties), notExpiredTime);

        // when
        rdbmsSessionManager.extendSession(userSession, refreshToken, accessToken);

        // then
        assertThat(userSession.getRefreshToken()).isNotBlank();
        AccessToken issuedAccessToken = AccessToken.deserialize(userSession.getAccessToken(), accessTokenProperties);
        assertThat(issuedAccessToken.getUserId()).isEqualTo(1L);
        assertThat(userSession.isExpired()).isFalse();
    }

    @Test
    @DisplayName("세션 연장 요청이 만료된 세션에 대해 실패한다")
    void whenExtendSession_ExpiredSession_thenFail() {
        // given
        UserProfile userProfile = new UserProfile("test-user", "email@test.com");
        User user = new User(userProfile);
        RefreshToken refreshToken = new RefreshToken();
        AccessToken accessToken = new AccessToken(1L, userProfile, List.of("profile"), accessTokenProperties);

        LocalDateTime expiredTime = LocalDateTime.now().minusMinutes(1);
        UserSession userSession = new UserSession(user,
                refreshToken.serialize(), accessToken.serialize(accessTokenProperties), expiredTime);

        // when & then
        assertThatThrownBy(() -> rdbmsSessionManager.extendSession(userSession, refreshToken, accessToken))
                .isInstanceOf(SessionExpiredException.class);
    }

    @Test
    @DisplayName("세션 연장 요청이 일치하지 않은 토큰에 대해 실패한다")
    void whenExtendSession_InvalidToken_thenFail() {
        // given
        UserProfile userProfile = new UserProfile("test-user", "email@test.com");
        User user = new User(userProfile);
        LocalDateTime notExpiredTime = LocalDateTime.now().plusMinutes(1);

        RefreshToken invalidRefreshToken = new RefreshToken();
        AccessToken invalidAccessToken = new AccessToken(1L, userProfile, List.of("profile"),
                accessTokenProperties);
        UserSession userSession = new UserSession(user, "refresh-token", "access-token", notExpiredTime);

        // when & then
        assertThatThrownBy(
                () -> rdbmsSessionManager.extendSession(userSession, invalidRefreshToken, invalidAccessToken))
                .isInstanceOf(InvalidCredentialException.class);
    }
}
