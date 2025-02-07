package sleppynavigators.studyupbackend.presentation.authentication;

import static io.restassured.RestAssured.with;
import static io.restassured.http.Method.POST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import java.time.LocalDateTime;
import java.util.List;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import sleppynavigators.studyupbackend.domain.authentication.UserCredential;
import sleppynavigators.studyupbackend.domain.authentication.session.UserSession;
import sleppynavigators.studyupbackend.domain.authentication.token.AccessToken;
import sleppynavigators.studyupbackend.domain.authentication.token.AccessTokenProperties;
import sleppynavigators.studyupbackend.domain.authentication.token.RefreshToken;
import sleppynavigators.studyupbackend.domain.user.User;
import sleppynavigators.studyupbackend.domain.user.vo.UserProfile;
import sleppynavigators.studyupbackend.exception.ErrorCode;
import sleppynavigators.studyupbackend.exception.ErrorResponse;
import sleppynavigators.studyupbackend.infrastructure.authentication.UserCredentialRepository;
import sleppynavigators.studyupbackend.infrastructure.authentication.oidc.GoogleOidcClient;
import sleppynavigators.studyupbackend.infrastructure.authentication.session.UserSessionRepository;
import sleppynavigators.studyupbackend.infrastructure.user.UserRepository;
import sleppynavigators.studyupbackend.presentation.authentication.dto.RefreshRequest;
import sleppynavigators.studyupbackend.presentation.authentication.dto.SignInRequest;
import sleppynavigators.studyupbackend.exception.network.InvalidCredentialException;
import sleppynavigators.studyupbackend.presentation.common.SuccessResponse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("AuthController 테스트")
class AuthControllerTest {

    @Autowired
    private UserCredentialRepository userCredentialRepository;

    @Autowired
    private AccessTokenProperties accessTokenProperties;

    @Autowired
    private UserSessionRepository userSessionRepository;

    @Autowired
    private UserRepository userRepository;

    @MockitoBean
    private GoogleOidcClient googleOidcClient;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        userSessionRepository.deleteAll();
        userCredentialRepository.deleteAll();
        userRepository.deleteAll();

        RestAssured.port = port;
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .build();
    }

    @TestConfiguration
    static class TestConfig {

        @Primary
        @Bean
        public GoogleOidcClient googleOidcClient() {
            return Mockito.mock(GoogleOidcClient.class);
        }
    }

    @Test
    @DisplayName("기존 회원의 구글 로그인을 성공적으로 수행한다")
    void memberGoogleSignIn_Success() {
        // given
        String idToken = "test-id-token";
        SignInRequest request = new SignInRequest(idToken);
        Claims idTokenClaims = Jwts.claims()
                .subject("test-subject")
                .add("name", "test-user")
                .add("email", "test-email")
                .build();
        given(googleOidcClient.deserialize(idToken)).willReturn(idTokenClaims);

        User user = new User(new UserProfile("test-user", "test-email"));
        UserCredential userCredential = new UserCredential("test-subject", "google", user);

        userRepository.save(user);
        userCredentialRepository.save(userCredential);

        // when
        ExtractableResponse<?> response = with()
                .body(request).queryParam("provider", "GOOGLE")
                .when().request(POST, "/auth/sign-in")
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(response.body().as(SuccessResponse.class).getData()).isNotNull();
    }

    @Test
    @DisplayName("신규 회원의 구글 로그인을 성공적으로 수행한다")
    void newMemberGoogleSignIn_Success() {
        // given
        String idToken = "test-id-token";
        SignInRequest request = new SignInRequest(idToken);
        Claims idTokenClaims = Jwts.claims()
                .subject("test-subject")
                .add("name", "test-user")
                .add("email", "test-email")
                .build();
        given(googleOidcClient.deserialize(idToken)).willReturn(idTokenClaims);

        assert userCredentialRepository.findBySubject("test-subject").isEmpty();

        // when
        ExtractableResponse<?> response = with()
                .body(request).queryParam("provider", "GOOGLE")
                .when().request(POST, "/auth/sign-in")
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(response.body().as(SuccessResponse.class).getData()).isNotNull();
        assertThat(userCredentialRepository.findBySubject("test-subject")).isNotEmpty();
    }

    @Test
    @DisplayName("유효하지 않은 id token으로 구글 로그인을 시도하면 예외가 발생한다")
    void whenInvalidIdToken_ThrowsInvalidCredentialException() {
        // given
        String idToken = "invalid-id-token";
        SignInRequest request = new SignInRequest(idToken);
        given(googleOidcClient.deserialize(idToken)).willThrow(new InvalidCredentialException());

        // when
        ExtractableResponse<?> response = with()
                .body(request).queryParam("provider", "GOOGLE")
                .when().request(POST, "/auth/sign-in")
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_BAD_REQUEST);
        assertThat(response.body().as(ErrorResponse.class).getCode())
                .isEqualTo(ErrorCode.INVALID_API.getCode());
    }

    @Test
    @DisplayName("토큰 갱신 요청이 성공적으로 수행된다")
    void refresh_Success() {
        // given
        User user = new User(new UserProfile("test-user", "test-email"));
        userRepository.saveAndFlush(user);

        AccessToken accessToken = new AccessToken(user.getId(), user.getUserProfile(), List.of("profile"),
                accessTokenProperties);
        RefreshToken refreshToken = new RefreshToken();
        LocalDateTime notExpiredTime = LocalDateTime.now().plusMinutes(1);
        RefreshRequest request = new RefreshRequest(accessToken.serialize(accessTokenProperties),
                refreshToken.serialize());

        UserSession userSession = new UserSession(user, refreshToken.serialize(),
                accessToken.serialize(accessTokenProperties), notExpiredTime);
        userSessionRepository.save(userSession);

        // when
        ExtractableResponse<?> response = with().body(request)
                .when().request(POST, "/auth/refresh")
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(response.body().as(SuccessResponse.class).getData()).isNotNull();
    }

    @Test
    @DisplayName("만료된 세션에 대해 토큰 갱신 요청을 수행하면 예외가 발생한다")
    void whenExpiredSession_ThrowsInvalidCredentialException() {
        // given
        User user = new User(new UserProfile("test-user", "test-email"));
        userRepository.saveAndFlush(user);

        AccessToken accessToken = new AccessToken(user.getId(), user.getUserProfile(), List.of("profile"),
                accessTokenProperties);
        RefreshToken refreshToken = new RefreshToken();
        LocalDateTime expiredTime = LocalDateTime.now().minusMinutes(1);
        RefreshRequest request = new RefreshRequest(accessToken.serialize(accessTokenProperties),
                refreshToken.serialize());

        UserSession userSession = new UserSession(user, refreshToken.serialize(),
                accessToken.serialize(accessTokenProperties), expiredTime);
        userSessionRepository.save(userSession);

        // when
        ExtractableResponse<?> response = with().body(request)
                .when().request(POST, "/auth/refresh")
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_BAD_REQUEST);
        assertThat(response.body().as(ErrorResponse.class).getCode())
                .isEqualTo(ErrorCode.SESSION_EXPIRED.getCode());
    }

    @Test
    @DisplayName("유효하지 않은 토큰으로 토큰 갱신 요청을 수행하면 예외가 발생한다")
    void whenInvalidToken_ThrowsInvalidCredentialException() {
        // given
        User user = new User(new UserProfile("test-user", "test-email"));
        userRepository.saveAndFlush(user);

        AccessToken accessToken = new AccessToken(user.getId(), user.getUserProfile(), List.of("profile"),
                accessTokenProperties);
        RefreshToken refreshToken = new RefreshToken();
        LocalDateTime notExpiredTime = LocalDateTime.now().plusMinutes(1);
        RefreshRequest request = new RefreshRequest("invalid-access-token", "invalid-refresh-token");

        UserSession userSession = new UserSession(user, refreshToken.serialize(),
                accessToken.serialize(accessTokenProperties), notExpiredTime);
        userSessionRepository.save(userSession);

        // when
        ExtractableResponse<?> response = with().body(request)
                .when().request(POST, "/auth/refresh")
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_BAD_REQUEST);
        assertThat(response.body().as(ErrorResponse.class).getCode())
                .isEqualTo(ErrorCode.INVALID_API.getCode());
    }
}
