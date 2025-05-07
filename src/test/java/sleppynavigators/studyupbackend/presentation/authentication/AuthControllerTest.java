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
import sleppynavigators.studyupbackend.common.RestAssuredBaseTest;
import sleppynavigators.studyupbackend.common.support.AuthSupport;
import sleppynavigators.studyupbackend.common.support.UserSupport;
import sleppynavigators.studyupbackend.domain.authentication.session.UserSession;
import sleppynavigators.studyupbackend.domain.user.User;
import sleppynavigators.studyupbackend.exception.ErrorCode;
import sleppynavigators.studyupbackend.presentation.authentication.dto.request.RefreshRequest;
import sleppynavigators.studyupbackend.presentation.authentication.dto.response.TokenResponse;

@DisplayName("[프레젠테이션] AuthController 테스트")
class AuthControllerTest extends RestAssuredBaseTest {

    @Autowired
    private UserSupport userSupport;

    @Autowired
    private AuthSupport authSupport;

    @Test
    @DisplayName("토큰 갱신 - 성공")
    void refresh_Success() {
        // given
        User userToRefresh = userSupport.registerUserToDB();
        LocalDateTime notExpiredTime = LocalDateTime.now().plusMinutes(1);
        UserSession sessionToRefresh = authSupport.registerUserSessionToDB(userToRefresh, notExpiredTime);

        RefreshRequest request = new RefreshRequest(sessionToRefresh.getAccessToken(),
                sessionToRefresh.getRefreshToken());

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
    @DisplayName("토큰 갱신 (만료된 세션) - 실패")
    void whenExpiredSession_ThrowsInvalidCredentialException() {
        // given
        User userToRefresh = userSupport.registerUserToDB();
        LocalDateTime expiredTime = LocalDateTime.now().minusMinutes(1);
        UserSession sessionToRefresh = authSupport.registerUserSessionToDB(userToRefresh, expiredTime);

        RefreshRequest request = new RefreshRequest(sessionToRefresh.getAccessToken(),
                sessionToRefresh.getRefreshToken());

        // when
        ExtractableResponse<?> response = with().body(request)
                .when().request(POST, "/auth/refresh")
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_BAD_REQUEST);
        assertThat(response.jsonPath().getString("code")).isEqualTo(ErrorCode.SESSION_EXPIRED.getCode());
        assertThat(response.jsonPath().getString("message"))
                .isEqualTo(ErrorCode.SESSION_EXPIRED.getDefaultMessage());
    }

    @Test
    @DisplayName("토큰 갱신 (존재하지 않는 세션) - 실패")
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
        assertThat(response.jsonPath().getString("message"))
                .contains("User session not found");
    }

    @Test
    @DisplayName("토큰 갱신 (토큰 불일치) - 실패")
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
        assertThat(response.jsonPath().getString("message"))
                .isEqualTo("Invalid refresh token or access token");
    }

    @Test
    @DisplayName("토큰 갱신 (JWT가 아닌 Access 토큰) - 실패")
    void whenNotJwtAccessToken_ThrowsInvalidCredentialException() {
        // given
        User userToRefresh = userSupport.registerUserToDB();
        LocalDateTime notExpiredTime = LocalDateTime.now().plusMinutes(1);
        UserSession sessionToRefresh = authSupport.registerUserSessionToDB(userToRefresh, notExpiredTime);

        RefreshRequest request = new RefreshRequest("not-jwt-access-token", sessionToRefresh.getRefreshToken());

        // when
        ExtractableResponse<?> response = with().body(request)
                .when().request(POST, "/auth/refresh")
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_BAD_REQUEST);
        assertThat(response.jsonPath().getString("code")).isEqualTo(ErrorCode.INVALID_CREDENTIALS.getCode());
        assertThat(response.jsonPath().getString("message"))
                .isEqualTo("Invalid access token");
    }
}
