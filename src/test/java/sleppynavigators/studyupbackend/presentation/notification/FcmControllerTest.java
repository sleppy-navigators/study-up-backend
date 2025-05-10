package sleppynavigators.studyupbackend.presentation.notification;

import static io.restassured.RestAssured.with;
import static io.restassured.http.Method.DELETE;
import static io.restassured.http.Method.POST;
import static io.restassured.http.Method.PUT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import sleppynavigators.studyupbackend.common.RestAssuredBaseTest;
import sleppynavigators.studyupbackend.common.support.AuthSupport;
import sleppynavigators.studyupbackend.common.support.FcmSupport;
import sleppynavigators.studyupbackend.common.support.UserSupport;
import sleppynavigators.studyupbackend.domain.notification.FcmToken;
import sleppynavigators.studyupbackend.domain.notification.FcmToken.DeviceType;
import sleppynavigators.studyupbackend.domain.user.User;
import sleppynavigators.studyupbackend.exception.ErrorCode;
import sleppynavigators.studyupbackend.infrastructure.notification.FcmClient;
import sleppynavigators.studyupbackend.infrastructure.notification.FcmTokenRepository;
import sleppynavigators.studyupbackend.presentation.notification.dto.request.FcmTokenRequest;
import sleppynavigators.studyupbackend.presentation.notification.dto.request.TestNotificationRequest;
import sleppynavigators.studyupbackend.presentation.notification.dto.request.TestNotificationResponse;
import sleppynavigators.studyupbackend.presentation.notification.dto.response.FcmTokenResponse;

@DisplayName("FCM 알림 API 테스트")
class FcmControllerTest extends RestAssuredBaseTest {

    @MockitoBean
    private FcmClient fcmClient;

    @Autowired
    private UserSupport userSupport;

    @Autowired
    private AuthSupport authSupport;

    @Autowired
    private FcmSupport fcmSupport;

    @Autowired
    private FcmTokenRepository fcmTokenRepository;

    private User currentUser;
    private String deviceId;
    private String token;
    private DeviceType deviceType;

    @BeforeEach
    void setUp() {
        when(fcmClient.sendMessage(anyString(), anyString(), anyString()))
                .thenReturn("mocked-message-id-1");

        when(fcmClient.sendMessage(anyString(), anyString(), anyString(), any(), any()))
                .thenReturn("mocked-message-id-2");

        currentUser = userSupport.registerUserToDB();
        String bearerToken = authSupport.createBearerToken(currentUser);
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .addHeader("Authorization", bearerToken)
                .setContentType(ContentType.JSON)
                .build();
                
        deviceId = "test-device-id";
        token = "test-fcm-token";
        deviceType = DeviceType.ANDROID;
    }

    @Test
    @DisplayName("FCM 토큰 등록에 성공한다")
    void upsertToken_Success() {
        // given
        FcmTokenRequest request = new FcmTokenRequest(token, deviceId, deviceType);

        // when
        ExtractableResponse<?> response = with()
                .body(request)
                .when().request(PUT, "/api/notifications/tokens")
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_CREATED);
        assertThat(response.jsonPath().getObject("data", FcmTokenResponse.class))
                .satisfies(data -> {
                    assertThat(this.validator.validate(data)).isEmpty();
                    assertThat(data.token()).isEqualTo(token);
                    assertThat(data.deviceId()).isEqualTo(deviceId);
                    assertThat(data.deviceType()).isEqualTo(deviceType);
                });
    }
    
    @Test
    @DisplayName("FCM 토큰 등록 시 토큰이 비어있으면 오류로 응답한다")
    void upsertToken_EmptyToken_Fail() {
        // given
        FcmTokenRequest request = new FcmTokenRequest("", deviceId, deviceType);

        // when
        ExtractableResponse<?> response = with()
                .body(request)
                .when().request(PUT, "/api/notifications/tokens")
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_BAD_REQUEST);
        assertThat(response.jsonPath().getString("code")).isEqualTo(ErrorCode.INVALID_API.getCode());
    }
    
    @Test
    @DisplayName("FCM 토큰 등록 시 디바이스 ID가 비어있으면 오류로 응답한다")
    void upsertToken_EmptyDeviceId_Fail() {
        // given
        FcmTokenRequest request = new FcmTokenRequest(token, "", deviceType);

        // when
        ExtractableResponse<?> response = with()
                .body(request)
                .when().request(PUT, "/api/notifications/tokens")
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_BAD_REQUEST);
        assertThat(response.jsonPath().getString("code")).isEqualTo(ErrorCode.INVALID_API.getCode());
    }
    
    @Test
    @DisplayName("FCM 토큰 등록 시 디바이스 타입이 null이면 오류로 응답한다")
    void upsertToken_NullDeviceType_Fail() {
        // given
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("token", token);
        requestMap.put("deviceId", deviceId);
        requestMap.put("deviceType", null);

        // when
        ExtractableResponse<?> response = with()
                .body(requestMap)
                .when().request(PUT, "/api/notifications/tokens")
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_BAD_REQUEST);
        assertThat(response.jsonPath().getString("code")).isEqualTo(ErrorCode.INVALID_API.getCode());
    }
    
    @Test
    @DisplayName("FCM 토큰 등록 시 이미 존재하는 디바이스 ID에 대해서는 토큰을 갱신한다")
    void upsertToken_ExistingDeviceId_UpdatesToken() {
        // given
        String initialToken = "initial-token";
        String updatedToken = "updated-token";
        
        // 먼저 토큰 등록
        fcmSupport.registerFcmTokenToDB(currentUser, initialToken, deviceId, deviceType);
        
        // 같은 디바이스 ID로 다른 토큰 등록 요청
        FcmTokenRequest updateRequest = new FcmTokenRequest(updatedToken, deviceId, deviceType);
        
        // when
        ExtractableResponse<?> response = with()
                .body(updateRequest)
                .when().request(PUT, "/api/notifications/tokens")
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_CREATED);
        assertThat(response.jsonPath().getObject("data", FcmTokenResponse.class))
                .satisfies(data -> {
                    assertThat(this.validator.validate(data)).isEmpty();
                    assertThat(data.token()).isEqualTo(updatedToken);
                    assertThat(data.deviceId()).isEqualTo(deviceId);
                });
        
        // 데이터베이스 확인
        assertThat(fcmTokenRepository.findByDeviceId(deviceId))
                .isPresent()
                .get()
                .satisfies(token -> assertThat(token.getToken()).isEqualTo(updatedToken));
    }

    @Test
    @DisplayName("특정 디바이스 FCM 토큰 삭제에 성공한다")
    void deleteToken_WithDeviceId_Success() {
        // given
        FcmToken fcmToken = fcmSupport.registerFcmTokenToDB(currentUser, token, deviceId, deviceType);

        // when
        ExtractableResponse<?> response = with()
                .queryParam("deviceId", deviceId)
                .when().request(DELETE, "/api/notifications/tokens")
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_NO_CONTENT);
        assertThat(fcmTokenRepository.findByDeviceId(deviceId)).isEmpty();
    }
    
    @Test
    @DisplayName("존재하지 않는 디바이스 ID에 대한 토큰 삭제도 정상 응답한다")
    void deleteToken_NonExistentDeviceId_StillSucceeds() {
        // given
        String nonExistentDeviceId = "non-existent-device-id";
        
        // when
        ExtractableResponse<?> response = with()
                .queryParam("deviceId", nonExistentDeviceId)
                .when().request(DELETE, "/api/notifications/tokens")
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_NO_CONTENT);
    }

    @Test
    @DisplayName("모든 FCM 토큰 삭제에 성공한다")
    void deleteAllTokens_Success() {
        // given
        fcmSupport.registerMultipleFcmTokensToDB(currentUser, 3);

        // when
        ExtractableResponse<?> response = with()
                .when().request(DELETE, "/api/notifications/tokens")
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_NO_CONTENT);
        assertThat(fcmTokenRepository.findAllByUserId(currentUser.getId())).isEmpty();
    }
    
    @Test
    @DisplayName("FCM 토큰이 없는 사용자도 모든 토큰 삭제에 성공한다")
    void deleteAllTokens_NoTokens_StillSucceeds() {
        // given
        assertThat(fcmTokenRepository.findAllByUserId(currentUser.getId())).isEmpty();

        // when
        ExtractableResponse<?> response = with()
                .when().request(DELETE, "/api/notifications/tokens")
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_NO_CONTENT);
    }

    @Test
    @DisplayName("테스트 알림 전송에 성공한다")
    void sendTestNotification_Success() throws MalformedURLException {
        // given
        String title = "테스트 제목";
        String body = "테스트 내용";
        URL imageUrl = new URL("https://example.com/image.jpg");
        Map<String, String> data = new HashMap<>();
        data.put("key", "value");

        // FCM 토큰을 먼저 등록해야 함
        fcmSupport.registerFcmTokenToDB(currentUser, token, deviceId, deviceType);

        TestNotificationRequest request = new TestNotificationRequest(title, body, imageUrl, data);

        // when
        ExtractableResponse<?> response = with()
                .body(request)
                .when().request(POST, "/api/notifications/test")
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(response.jsonPath().getList("data", TestNotificationResponse.class))
                .isNotEmpty()
                .allSatisfy(item -> {
                    assertThat(this.validator.validate(item)).isEmpty();
                    assertThat(item.messageId()).isNotBlank();
                    assertThat(item.timestamp()).isNotNull();
                });
    }
    
    @Test
    @DisplayName("테스트 알림 전송 시 제목이 비어있으면 오류로 응답한다")
    void sendTestNotification_EmptyTitle_Fail() {
        // given
        String title = "";
        String body = "테스트 내용";
        Map<String, String> data = new HashMap<>();
        data.put("key", "value");

        // FCM 토큰을 먼저 등록해야 함
        fcmSupport.registerFcmTokenToDB(currentUser, token, deviceId, deviceType);

        TestNotificationRequest request = new TestNotificationRequest(title, body, null, data);

        // when
        ExtractableResponse<?> response = with()
                .body(request)
                .when().request(POST, "/api/notifications/test")
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_BAD_REQUEST);
        assertThat(response.jsonPath().getString("code")).isEqualTo(ErrorCode.INVALID_API.getCode());
    }
    
    @Test
    @DisplayName("테스트 알림 전송 시 내용이 비어있으면 오류로 응답한다")
    void sendTestNotification_EmptyBody_Fail() {
        // given
        String title = "테스트 제목";
        String body = "";
        Map<String, String> data = new HashMap<>();
        data.put("key", "value");

        // FCM 토큰을 먼저 등록해야 함
        fcmSupport.registerFcmTokenToDB(currentUser, token, deviceId, deviceType);

        TestNotificationRequest request = new TestNotificationRequest(title, body, null, data);

        // when
        ExtractableResponse<?> response = with()
                .body(request)
                .when().request(POST, "/api/notifications/test")
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_BAD_REQUEST);
        assertThat(response.jsonPath().getString("code")).isEqualTo(ErrorCode.INVALID_API.getCode());
    }
    
    @Test
    @DisplayName("등록된 FCM 토큰이 없는 사용자가 테스트 알림 전송 시 오류로 응답한다")
    void sendTestNotification_NoRegisteredTokens_Fail() {
        // given
        String title = "테스트 제목";
        String body = "테스트 내용";
        Map<String, String> data = new HashMap<>();
        data.put("key", "value");

        // 토큰 미등록 상태 확인
        assertThat(fcmTokenRepository.findAllByUserId(currentUser.getId())).isEmpty();

        TestNotificationRequest request = new TestNotificationRequest(title, body, null, data);

        // when
        ExtractableResponse<?> response = with()
                .body(request)
                .when().request(POST, "/api/notifications/test")
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_NOT_FOUND);
        assertThat(response.jsonPath().getString("code")).isEqualTo(ErrorCode.ENTITY_NOT_FOUND.getCode());
        assertThat(response.jsonPath().getString("message"))
                .contains("No registered devices found for the user");
    }
}
