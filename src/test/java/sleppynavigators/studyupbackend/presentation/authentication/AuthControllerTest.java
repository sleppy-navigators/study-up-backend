package sleppynavigators.studyupbackend.presentation.authentication;

import static io.restassured.RestAssured.with;
import static io.restassured.http.Method.POST;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import java.time.LocalDateTime;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sleppynavigators.studyupbackend.common.support.AuthSupport;
import sleppynavigators.studyupbackend.common.support.UserSupport;
import sleppynavigators.studyupbackend.domain.authentication.session.UserSession;
import sleppynavigators.studyupbackend.domain.user.User;
import sleppynavigators.studyupbackend.exception.ErrorCode;
import sleppynavigators.studyupbackend.presentation.authentication.dto.request.RefreshRequest;
import sleppynavigators.studyupbackend.common.RestAssuredBaseTest;
import sleppynavigators.studyupbackend.presentation.authentication.dto.response.TokenResponse;

@DisplayName("AuthController API 테스트")
class AuthControllerTest extends RestAssuredBaseTest {

    @Autowired
    private UserSupport userSupport;

    @Autowired
    private AuthSupport authSupport;

    @Test
    @DisplayName("토큰 갱신 요청이 성공적으로 수행된다")
    void refresh_Success() {
        // given
        User userToRefresh = userSupport.registerUserToDB();
        LocalDateTime notExpiredTime = LocalDateTime.now().plusMinutes(1);
        UserSession sessionToRefresh = authSupport.registerUserSessionToDB(userToRefresh, notExpiredTime);

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
        User userToRefresh = userSupport.registerUserToDB();
        LocalDateTime expiredTime = LocalDateTime.now().minusMinutes(1);
        UserSession sessionToRefresh = authSupport.registerUserSessionToDB(userToRefresh, expiredTime);

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
        User userToRefresh = userSupport.registerUserToDB();
        String accessToken = authSupport.createAccessToken(userToRefresh);
        String refreshToken = authSupport.createRefreshToken();

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
        User userToRefresh = userSupport.registerUserToDB();
        LocalDateTime notExpiredTime = LocalDateTime.now().plusMinutes(1);
        UserSession sessionToRefresh = authSupport.registerUserSessionToDB(userToRefresh, notExpiredTime);

        String accessToken = authSupport.createAccessToken(userToRefresh);
        String refreshToken = authSupport.createRefreshToken();
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
        User userToRefresh = userSupport.registerUserToDB();
        LocalDateTime notExpiredTime = LocalDateTime.now().plusMinutes(1);
        UserSession sessionToRefresh = authSupport.registerUserSessionToDB(userToRefresh, notExpiredTime);

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
}
