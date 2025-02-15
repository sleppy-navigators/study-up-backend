package sleppynavigators.studyupbackend.presentation.authentication;

import static io.restassured.RestAssured.with;
import static io.restassured.http.Method.POST;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import java.time.LocalDateTime;
import java.util.List;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import sleppynavigators.studyupbackend.domain.authentication.session.UserSession;
import sleppynavigators.studyupbackend.domain.authentication.token.AccessToken;
import sleppynavigators.studyupbackend.domain.authentication.token.AccessTokenProperties;
import sleppynavigators.studyupbackend.domain.authentication.token.RefreshToken;
import sleppynavigators.studyupbackend.domain.user.User;
import sleppynavigators.studyupbackend.exception.ErrorCode;
import sleppynavigators.studyupbackend.infrastructure.authentication.session.UserSessionRepository;
import sleppynavigators.studyupbackend.infrastructure.user.UserRepository;
import sleppynavigators.studyupbackend.presentation.authentication.dto.request.RefreshRequest;
import sleppynavigators.studyupbackend.presentation.common.DatabaseCleaner;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("AuthController API 테스트")
class AuthControllerTest {

    @Autowired
    private AccessTokenProperties accessTokenProperties;

    @Autowired
    private UserSessionRepository userSessionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .build();
    }

    @AfterEach
    void tearDown() {
        databaseCleaner.execute();
        RestAssured.reset();
    }

    @Test
    @DisplayName("토큰 갱신 요청이 성공적으로 수행된다")
    void refresh_Success() {
        // given
        User user = new User("test-user", "test-email");
        userRepository.save(user);

        AccessToken accessToken =
                new AccessToken(user.getId(), user.getUserProfile(), List.of("profile"), accessTokenProperties);
        RefreshToken refreshToken = new RefreshToken();
        LocalDateTime notExpiredTime = LocalDateTime.now().plusMinutes(1);
        RefreshRequest request =
                new RefreshRequest(accessToken.serialize(accessTokenProperties), refreshToken.serialize());

        UserSession userSession = UserSession.builder()
                .user(user)
                .refreshToken(refreshToken.serialize())
                .accessToken(accessToken.serialize(accessTokenProperties))
                .expiration(notExpiredTime)
                .build();
        userSessionRepository.save(userSession);

        // when
        ExtractableResponse<?> response = with().body(request)
                .when().request(POST, "/auth/refresh")
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(response.jsonPath().getString("data.accessToken")).isNotBlank();
        assertThat(response.jsonPath().getString("data.refreshToken")).isNotBlank();
    }

    @Test
    @DisplayName("만료된 세션에 대해 토큰 갱신 요청을 수행하면 예외가 발생한다")
    void whenExpiredSession_ThrowsInvalidCredentialException() {
        // given
        User user = new User("test-user", "test-email");
        userRepository.save(user);

        AccessToken accessToken =
                new AccessToken(user.getId(), user.getUserProfile(), List.of("profile"), accessTokenProperties);
        RefreshToken refreshToken = new RefreshToken();
        LocalDateTime expiredTime = LocalDateTime.now().minusMinutes(1);
        RefreshRequest request =
                new RefreshRequest(accessToken.serialize(accessTokenProperties), refreshToken.serialize());

        UserSession userSession = UserSession.builder()
                .user(user)
                .refreshToken(refreshToken.serialize())
                .accessToken(accessToken.serialize(accessTokenProperties))
                .expiration(expiredTime)
                .build();
        userSessionRepository.save(userSession);

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
    @DisplayName("유효하지 않은 토큰으로 토큰 갱신 요청을 수행하면 예외가 발생한다")
    void whenInvalidToken_ThrowsInvalidCredentialException() {
        // given
        User user = new User("test-user", "test-email");
        userRepository.save(user);

        AccessToken accessToken =
                new AccessToken(user.getId(), user.getUserProfile(), List.of("profile"), accessTokenProperties);
        RefreshToken refreshToken = new RefreshToken();
        LocalDateTime notExpiredTime = LocalDateTime.now().plusMinutes(1);
        RefreshRequest request = new RefreshRequest("invalid-access-token", "invalid-refresh-token");

        UserSession userSession = UserSession.builder()
                .user(user)
                .refreshToken(refreshToken.serialize())
                .accessToken(accessToken.serialize(accessTokenProperties))
                .expiration(notExpiredTime)
                .build();
        userSessionRepository.save(userSession);

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
}
