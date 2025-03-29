package sleppynavigators.studyupbackend.presentation.authentication;

import static io.restassured.RestAssured.with;
import static io.restassured.http.Method.POST;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import java.time.LocalDateTime;
import java.util.List;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import sleppynavigators.studyupbackend.domain.authentication.session.UserSession;
import sleppynavigators.studyupbackend.domain.authentication.token.AccessToken;
import sleppynavigators.studyupbackend.domain.authentication.token.AccessTokenProperties;
import sleppynavigators.studyupbackend.domain.authentication.token.RefreshToken;
import sleppynavigators.studyupbackend.domain.user.User;
import sleppynavigators.studyupbackend.domain.user.vo.UserProfile;
import sleppynavigators.studyupbackend.exception.ErrorCode;
import sleppynavigators.studyupbackend.infrastructure.authentication.session.UserSessionRepository;
import sleppynavigators.studyupbackend.infrastructure.user.UserRepository;
import sleppynavigators.studyupbackend.presentation.authentication.dto.request.RefreshRequest;
import sleppynavigators.studyupbackend.common.RestAssuredBaseTest;
import sleppynavigators.studyupbackend.presentation.authentication.dto.response.TokenResponse;

@DisplayName("AuthController API 테스트")
class AuthControllerTest extends RestAssuredBaseTest {

    @Autowired
    private AccessTokenProperties accessTokenProperties;

    @Autowired
    private UserSessionRepository userSessionRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("토큰 갱신 요청이 성공적으로 수행된다")
    void refresh_Success() {
        // given
        User userToRefresh = TestFixtureMother.registerUser(userRepository);
        LocalDateTime notExpiredTime = LocalDateTime.now().plusMinutes(1);
        UserSession sessionToRefresh = TestFixtureMother
                .registerUserSession(userToRefresh, notExpiredTime, userSessionRepository, accessTokenProperties);

        RefreshRequest request =
                new RefreshRequest(sessionToRefresh.getAccessToken(), sessionToRefresh.getRefreshToken());

        // when
        ExtractableResponse<?> response = with().body(request)
                .when().request(POST, "/auth/refresh")
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(response.jsonPath().getObject("data", TokenResponse.class))
                .satisfies(data -> assertThat(this.validator.validate(data)).isEmpty());
    }

    @Test
    @DisplayName("만료된 세션에 대해 토큰 갱신 요청을 수행하면 예외가 발생한다")
    void whenExpiredSession_ThrowsInvalidCredentialException() {
        // given
        User userToRefresh = TestFixtureMother.registerUser(userRepository);
        LocalDateTime expiredTime = LocalDateTime.now().minusMinutes(1);
        UserSession sessionToRefresh = TestFixtureMother
                .registerUserSession(userToRefresh, expiredTime, userSessionRepository, accessTokenProperties);

        RefreshRequest request =
                new RefreshRequest(sessionToRefresh.getAccessToken(), sessionToRefresh.getRefreshToken());

        // when
        ExtractableResponse<?> response = with().body(request)
                .when().request(POST, "/auth/refresh")
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_BAD_REQUEST);
        assertThat(response.jsonPath().getString("code")).isEqualTo(ErrorCode.SESSION_EXPIRED.getCode());
        assertThat(response.jsonPath().getString("message")).isEqualTo(ErrorCode.SESSION_EXPIRED.getDefaultMessage());
    }

    @Test
    @DisplayName("존재하지 않는 세션에 대해 토큰 갱신 요청을 수행하면 예외가 발생한다")
    void whenNonExistentSession_ThrowsInvalidCredentialException() {
        // given
        User userToRefresh = TestFixtureMother.registerUser(userRepository);
        String accessToken = TestFixtureMother.createAccessToken(userToRefresh, accessTokenProperties);
        String refreshToken = TestFixtureMother.createRefreshToken();
        RefreshRequest request = new RefreshRequest(accessToken, refreshToken);

        // when
        ExtractableResponse<?> response = with().body(request)
                .when().request(POST, "/auth/refresh")
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_NOT_FOUND);
        assertThat(response.jsonPath().getString("code")).isEqualTo(ErrorCode.ENTITY_NOT_FOUND.getCode());
        assertThat(response.jsonPath().getString("message")).isEqualTo(
                ErrorCode.ENTITY_NOT_FOUND.getDefaultMessage());
    }

    @Test
    @DisplayName("토큰 정보가 일치하지 않을 경우 예외가 발생한다")
    void whenUnMatchedToken_ThrowsInvalidCredentialException() {
        // given
        User userToRefresh = TestFixtureMother.registerUser(userRepository);
        LocalDateTime notExpiredTime = LocalDateTime.now().plusMinutes(1);
        UserSession sessionToRefresh = TestFixtureMother
                .registerUserSession(userToRefresh, notExpiredTime, userSessionRepository, accessTokenProperties);

        String accessToken = TestFixtureMother.createAccessToken(userToRefresh, accessTokenProperties);
        String refreshToken = TestFixtureMother.createRefreshToken();
        RefreshRequest request = new RefreshRequest(accessToken, refreshToken);

        // when
        ExtractableResponse<?> response = with().body(request)
                .when().request(POST, "/auth/refresh")
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_BAD_REQUEST);
        assertThat(response.jsonPath().getString("code")).isEqualTo(ErrorCode.INVALID_CREDENTIALS.getCode());
        assertThat(response.jsonPath().getString("message")).isEqualTo(
                ErrorCode.INVALID_CREDENTIALS.getDefaultMessage());
    }

    @Test
    @DisplayName("JWT가 아닌 Access 토큰으로 토큰 갱신 요청을 수행하면 예외가 발생한다")
    void whenNotJwtAccessToken_ThrowsInvalidCredentialException() {
        // given
        User userToRefresh = TestFixtureMother.registerUser(userRepository);
        LocalDateTime notExpiredTime = LocalDateTime.now().plusMinutes(1);
        UserSession sessionToRefresh = TestFixtureMother
                .registerUserSession(userToRefresh, notExpiredTime, userSessionRepository, accessTokenProperties);

        RefreshRequest request =
                new RefreshRequest("not-jwt-access-token", sessionToRefresh.getRefreshToken());

        // when
        ExtractableResponse<?> response = with().body(request)
                .when().request(POST, "/auth/refresh")
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_BAD_REQUEST);
        assertThat(response.jsonPath().getString("code")).isEqualTo(ErrorCode.INVALID_CREDENTIALS.getCode());
        assertThat(response.jsonPath().getString("message")).isEqualTo(
                ErrorCode.INVALID_CREDENTIALS.getDefaultMessage());
    }

    @Transactional
    private static class TestFixtureMother {

        /**
         * Generate a test user and save it to the database. `username` and `email` are set to "test-user" and
         * "test-email" respectively.
         *
         * @param userRepository UserRepository to save the user
         * @return The saved user
         */
        static User registerUser(UserRepository userRepository) {
            User user = new User("test-user", "test-email");
            return userRepository.save(user);
        }

        /**
         * Generate a test user session and save it to the database. The session is created with the given user, and
         * expires in the given expiration time.
         *
         * @param user                  The user for the session
         * @param expiration            The expiration time for the session
         * @param userSessionRepository UserSessionRepository to save the user session
         * @param accessTokenProperties The properties for the access token
         * @return The saved user session
         */
        static UserSession registerUserSession(User user, LocalDateTime expiration,
                                               UserSessionRepository userSessionRepository,
                                               AccessTokenProperties accessTokenProperties) {
            Long userId = user.getId();
            UserProfile userProfile = user.getUserProfile();
            List<String> authorities = List.of("profile");

            AccessToken accessToken = new AccessToken(userId, userProfile, authorities, accessTokenProperties);
            RefreshToken refreshToken = new RefreshToken();

            UserSession userSession = UserSession.builder()
                    .user(user)
                    .refreshToken(refreshToken.serialize())
                    .accessToken(accessToken.serialize(accessTokenProperties))
                    .expiration(expiration)
                    .build();
            return userSessionRepository.save(userSession);
        }

        /**
         * Generate access token for the given user. The token can be used to authenticate the user for testing.
         *
         * @param user                  The user for testing
         * @param accessTokenProperties The properties for the access token
         * @return The generated access token as a string
         */
        static String createAccessToken(User user, AccessTokenProperties accessTokenProperties) {
            Long userId = user.getId();
            UserProfile userProfile = user.getUserProfile();
            List<String> authorities = List.of("profile");

            return new AccessToken(userId, userProfile, authorities, accessTokenProperties)
                    .serialize(accessTokenProperties);
        }

        /**
         * Generate a refresh token for the given user. The token can be used to authenticate the user for testing.
         *
         * @return The generated refresh token as a string
         */
        static String createRefreshToken() {
            return new RefreshToken().serialize();
        }
    }
}
