package sleppynavigators.studyupbackend.application.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sleppynavigators.studyupbackend.common.ApplicationBaseTest;
import sleppynavigators.studyupbackend.domain.notification.FcmToken;
import sleppynavigators.studyupbackend.domain.notification.FcmToken.DeviceType;
import sleppynavigators.studyupbackend.domain.user.User;
import sleppynavigators.studyupbackend.exception.database.EntityNotFoundException;
import sleppynavigators.studyupbackend.infrastructure.notification.FcmTokenRepository;
import sleppynavigators.studyupbackend.infrastructure.user.UserRepository;
import sleppynavigators.studyupbackend.presentation.notification.dto.FcmTokenRequest;

import java.util.List;

@DisplayName("FcmTokenService 테스트")
class FcmTokenServiceTest extends ApplicationBaseTest {

    @Autowired
    private FcmTokenService fcmTokenService;

    @Autowired
    private FcmTokenRepository fcmTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("새로운 FCM 토큰을 등록한다")
    void upsertToken_RegistersNewToken() {
        User user = createUser("test-user");
        String token = "test-token";
        String deviceId = "device-id-1";
        DeviceType deviceType = DeviceType.WEB;

        FcmTokenRequest request = new FcmTokenRequest(token, deviceId, deviceType);
        FcmToken fcmToken = fcmTokenService.upsertToken(user.getId(), request);

        assertThat(fcmToken).isNotNull();
        assertThat(fcmToken.getId()).isNotNull();
        assertThat(fcmToken.getToken()).isEqualTo(token);
        assertThat(fcmToken.getDeviceId()).isEqualTo(deviceId);
        assertThat(fcmToken.getDeviceType()).isEqualTo(deviceType);
        assertThat(fcmToken.getUser().getId()).isEqualTo(user.getId());

        FcmToken savedToken = fcmTokenRepository.findByDeviceId(deviceId).orElse(null);
        assertThat(savedToken).isNotNull();
        assertThat(savedToken.getId()).isEqualTo(fcmToken.getId());
    }

    @Test
    @DisplayName("이미 존재하는 디바이스 ID에 대해 토큰을 갱신한다")
    void upsertToken_UpdatesExistingToken() {
        User user = createUser("test-user");
        String oldToken = "old-token";
        String newToken = "new-token";
        String deviceId = "device-id-2";
        DeviceType deviceType = DeviceType.ANDROID;

        FcmTokenRequest initialRequest = new FcmTokenRequest(oldToken, deviceId, deviceType);
        FcmToken initialToken = fcmTokenService.upsertToken(user.getId(), initialRequest);
        assertThat(initialToken.getToken()).isEqualTo(oldToken);

        FcmTokenRequest updateRequest = new FcmTokenRequest(newToken, deviceId, deviceType);
        FcmToken updatedToken = fcmTokenService.upsertToken(user.getId(), updateRequest);
        
        assertThat(updatedToken.getId()).isEqualTo(initialToken.getId());
        assertThat(updatedToken.getToken()).isEqualTo(newToken);
        assertThat(updatedToken.getDeviceId()).isEqualTo(deviceId);

        FcmToken savedToken = fcmTokenRepository.findByDeviceId(deviceId).orElse(null);
        assertThat(savedToken).isNotNull();
        assertThat(savedToken.getToken()).isEqualTo(newToken);
    }

    @Test
    @DisplayName("존재하지 않는 사용자에 대해 토큰 등록을 시도하면 예외가 발생한다")
    void upsertToken_ThrowsException_WhenUserNotFound() {
        Long nonExistentUserId = 9999L;
        String token = "test-token";
        String deviceId = "device-id-3";
        DeviceType deviceType = DeviceType.WEB;

        FcmTokenRequest request = new FcmTokenRequest(token, deviceId, deviceType);
        assertThatThrownBy(() -> 
            fcmTokenService.upsertToken(nonExistentUserId, request))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("User not found");
    }

    @Test
    @DisplayName("디바이스 ID로 FCM 토큰을 삭제한다")
    void deleteTokenByDeviceId_DeletesToken() {
        User user = createUser("test-user");
        String token = "test-token";
        String deviceId = "device-id-4";
        DeviceType deviceType = DeviceType.IOS;

        FcmTokenRequest request = new FcmTokenRequest(token, deviceId, deviceType);
        fcmTokenService.upsertToken(user.getId(), request);
        assertThat(fcmTokenRepository.findByDeviceId(deviceId)).isPresent();

        fcmTokenService.deleteTokenByDeviceId(deviceId);

        assertThat(fcmTokenRepository.findByDeviceId(deviceId)).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 디바이스 ID에 대해 토큰 삭제를 시도해도 예외가 발생하지 않는다")
    void deleteTokenByDeviceId_DoesNothing_WhenDeviceIdNotFound() {
        String nonExistentDeviceId = "non-existent-device-id";
        
        fcmTokenService.deleteTokenByDeviceId(nonExistentDeviceId);
    }

    @Test
    @DisplayName("사용자의 모든 FCM 토큰을 삭제한다")
    void deleteAllTokensByUserId_DeletesAllTokens() {
        User user = createUser("test-user-for-delete-all");
        
        FcmTokenRequest request1 = new FcmTokenRequest("token1", "device-id-5", DeviceType.ANDROID);
        FcmTokenRequest request2 = new FcmTokenRequest("token2", "device-id-6", DeviceType.IOS);
        FcmTokenRequest request3 = new FcmTokenRequest("token3", "device-id-7", DeviceType.WEB);
        
        fcmTokenService.upsertToken(user.getId(), request1);
        fcmTokenService.upsertToken(user.getId(), request2);
        fcmTokenService.upsertToken(user.getId(), request3);

        List<FcmToken> userTokens = fcmTokenRepository.findAllByUserId(user.getId());
        assertThat(userTokens).hasSize(3);

        fcmTokenService.deleteAllTokensByUserId(user.getId());

        List<FcmToken> remainingTokens = fcmTokenRepository.findAllByUserId(user.getId());
        assertThat(remainingTokens).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 사용자에 대해 모든 토큰 삭제를 시도하면 예외가 발생한다")
    void deleteAllTokensByUserId_ThrowsException_WhenUserNotFound() {
        Long nonExistentUserId = 9999L;

        assertThatThrownBy(() -> 
            fcmTokenService.deleteAllTokensByUserId(nonExistentUserId))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("User not found");
    }

    private User createUser(String name) {
        User user = new User(name, name + "@example.com");
        return userRepository.save(user);
    }
}
