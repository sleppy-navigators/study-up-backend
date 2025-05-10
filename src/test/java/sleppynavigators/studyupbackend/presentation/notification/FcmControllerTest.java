package sleppynavigators.studyupbackend.presentation.notification;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import sleppynavigators.studyupbackend.common.RestAssuredBaseTest;
import sleppynavigators.studyupbackend.common.support.AuthSupport;
import sleppynavigators.studyupbackend.common.support.UserSupport;
import sleppynavigators.studyupbackend.domain.notification.FcmToken;
import sleppynavigators.studyupbackend.domain.notification.FcmToken.DeviceType;
import sleppynavigators.studyupbackend.domain.user.User;
import sleppynavigators.studyupbackend.infrastructure.notification.FcmClient;
import sleppynavigators.studyupbackend.infrastructure.notification.FcmTokenRepository;
import sleppynavigators.studyupbackend.presentation.notification.dto.request.FcmTokenDeleteRequest;
import sleppynavigators.studyupbackend.presentation.notification.dto.request.FcmTokenRequest;

@DisplayName("FcmController API 테스트")
class FcmControllerTest extends RestAssuredBaseTest {

    @MockitoBean
    private FcmClient fcmClient;

    @Autowired
    private FcmTokenRepository fcmTokenRepository;

    @Autowired
    private UserSupport userSupport;

    @Autowired
    private AuthSupport authSupport;
    
    @BeforeEach
    void setUpMocks() {
        when(fcmClient.sendMessage(anyString(), anyString(), anyString()))
            .thenReturn("mocked-message-id-1");
            
        when(fcmClient.sendMessage(anyString(), anyString(), anyString(), any(), any()))
            .thenReturn("mocked-message-id-2");
    }

    @Test
    @DisplayName("FCM 토큰 등록/갱신 요청이 성공적으로 수행된다")
    void upsertToken_Success() {
        User user = userSupport.registerUserToDB();
        String accessToken = authSupport.createAccessToken(user);

        FcmTokenRequest request = new FcmTokenRequest(
                "test-fcm-token-1", 
                "test-device-id-1", 
                DeviceType.WEB
        );

        ExtractableResponse<Response> response = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + accessToken)
                .body(request)
                .when()
                .put("/api/notifications/tokens")
                .then()
                .log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_CREATED);
        assertThat(response.jsonPath().getString("token")).isEqualTo("test-fcm-token-1");
        assertThat(response.jsonPath().getString("deviceId")).isEqualTo("test-device-id-1");
        assertThat(response.jsonPath().getString("deviceType")).isEqualTo("WEB");

        FcmToken savedToken = fcmTokenRepository.findByDeviceId("test-device-id-1").orElse(null);
        assertThat(savedToken).isNotNull();
        assertThat(savedToken.getToken()).isEqualTo("test-fcm-token-1");
        assertThat(savedToken.getDeviceId()).isEqualTo("test-device-id-1");
        assertThat(savedToken.getDeviceType()).isEqualTo(DeviceType.WEB);
        assertThat(savedToken.getUser().getId()).isEqualTo(user.getId());
    }

    @Test
    @DisplayName("이미 존재하는 디바이스 ID에 대해 토큰 갱신 요청이 성공적으로 수행된다")
    void upsertToken_UpdatesExistingToken() {
        User user = userSupport.registerUserToDB();
        String accessToken = authSupport.createAccessToken(user);

        // 1. 최초 토큰 등록
        FcmTokenRequest initialRequest = new FcmTokenRequest(
                "initial-token", 
                "test-device-id-2", 
                DeviceType.ANDROID
        );

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + accessToken)
                .body(initialRequest)
                .when()
                .put("/api/notifications/tokens")
                .then()
                .statusCode(HttpStatus.SC_CREATED);

        // 2. 토큰 갱신
        FcmTokenRequest updateRequest = new FcmTokenRequest(
                "updated-token", 
                "test-device-id-2", 
                DeviceType.ANDROID
        );

        ExtractableResponse<Response> response = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + accessToken)
                .body(updateRequest)
                .when()
                .put("/api/notifications/tokens")
                .then()
                .log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_CREATED);
        assertThat(response.jsonPath().getString("token")).isEqualTo("updated-token");

        FcmToken updatedToken = fcmTokenRepository.findByDeviceId("test-device-id-2").orElse(null);
        assertThat(updatedToken).isNotNull();
        assertThat(updatedToken.getToken()).isEqualTo("updated-token");
    }

    @Test
    @DisplayName("FCM 토큰 삭제 요청이 성공적으로 수행된다")
    void deleteToken_Success() {
        User user = userSupport.registerUserToDB();
        String accessToken = authSupport.createAccessToken(user);

        // 1. 토큰 등록
        FcmTokenRequest registerRequest = new FcmTokenRequest(
                "token-to-delete", 
                "test-device-id-3", 
                DeviceType.IOS
        );

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + accessToken)
                .body(registerRequest)
                .when()
                .put("/api/notifications/tokens")
                .then()
                .statusCode(HttpStatus.SC_CREATED);

        // 토큰이 등록되었는지 확인
        assertThat(fcmTokenRepository.findByDeviceId("test-device-id-3")).isPresent();

        // 2. 토큰 삭제
        FcmTokenDeleteRequest deleteRequest = new FcmTokenDeleteRequest("test-device-id-3");

        ExtractableResponse<Response> response = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + accessToken)
                .body(deleteRequest)
                .when()
                .delete("/api/notifications/tokens")
                .then()
                .log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_NO_CONTENT);

        // 토큰이 삭제되었는지 확인
        assertThat(fcmTokenRepository.findByDeviceId("test-device-id-3")).isEmpty();
    }

    @Test
    @DisplayName("다른 사용자의 FCM 토큰은 삭제할 수 없다")
    void deleteToken_CannotDeleteOtherUsersToken() {
        // 첫 번째 사용자가 토큰 등록
        User user1 = userSupport.registerUserToDB();
        String accessToken1 = authSupport.createAccessToken(user1);

        FcmTokenRequest registerRequest = new FcmTokenRequest(
                "token-of-user1", 
                "test-device-id-special", 
                DeviceType.IOS
        );

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + accessToken1)
                .body(registerRequest)
                .when()
                .put("/api/notifications/tokens")
                .then()
                .statusCode(HttpStatus.SC_CREATED);

        // 토큰이 등록되었는지 확인
        assertThat(fcmTokenRepository.findByDeviceId("test-device-id-special")).isPresent();

        // 두 번째 사용자가 첫 번째 사용자의 토큰 삭제 시도
        User user2 = userSupport.registerUserToDB();
        String accessToken2 = authSupport.createAccessToken(user2);

        FcmTokenDeleteRequest deleteRequest = new FcmTokenDeleteRequest("test-device-id-special");

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + accessToken2)
                .body(deleteRequest)
                .when()
                .delete("/api/notifications/tokens")
                .then()
                .statusCode(HttpStatus.SC_NO_CONTENT);

        // 토큰이 여전히 존재해야 함 (삭제되지 않음)
        assertThat(fcmTokenRepository.findByDeviceId("test-device-id-special")).isPresent();
    }

    @Test
    @DisplayName("FCM 토큰 전체 삭제 요청이 성공적으로 수행된다")
    void deleteAllTokens_Success() {
        User user = userSupport.registerUserToDB();
        String accessToken = authSupport.createAccessToken(user);

        // 여러 토큰 등록
        for (int i = 0; i < 3; i++) {
            FcmTokenRequest registerRequest = new FcmTokenRequest(
                    "token-" + i, 
                    "test-device-id-all-" + i, 
                    DeviceType.WEB
            );

            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + accessToken)
                    .body(registerRequest)
                    .when()
                    .put("/api/notifications/tokens")
                    .then()
                    .statusCode(HttpStatus.SC_CREATED);
        }

        // 사용자의 모든 토큰 삭제
        ExtractableResponse<Response> response = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .delete("/api/notifications/tokens/all")
                .then()
                .log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_NO_CONTENT);

        // 사용자의 모든 토큰이 삭제되었는지 확인
        assertThat(fcmTokenRepository.findAllByUserId(user.getId())).isEmpty();
    }

    @Test
    @DisplayName("인증되지 않은 사용자의 FCM 토큰 등록 요청은 401 상태 코드를 반환한다")
    void upsertToken_Unauthorized_Returns401() {
        FcmTokenRequest request = new FcmTokenRequest(
                "test-token", 
                "test-device-id", 
                DeviceType.WEB
        );

        ExtractableResponse<Response> response = given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .put("/api/notifications/tokens")
                .then()
                .log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_UNAUTHORIZED);
    }

    @Test
    @DisplayName("요청 필드가 누락된 FCM 토큰 등록 요청은 400 상태 코드를 반환한다")
    void upsertToken_MissingRequiredField_Returns400() {
        User user = userSupport.registerUserToDB();
        String accessToken = authSupport.createAccessToken(user);

        FcmTokenRequest invalidRequest = new FcmTokenRequest(
                "test-token", 
                "",
                DeviceType.WEB
        );

        ExtractableResponse<Response> response = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + accessToken)
                .body(invalidRequest)
                .when()
                .put("/api/notifications/tokens")
                .then()
                .log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_BAD_REQUEST);
    }
}
