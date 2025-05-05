package sleppynavigators.studyupbackend.presentation.notification;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sleppynavigators.studyupbackend.application.notification.FcmNotificationService;
import sleppynavigators.studyupbackend.application.notification.FcmTokenService;
import sleppynavigators.studyupbackend.domain.notification.FcmToken;
import sleppynavigators.studyupbackend.presentation.authentication.filter.UserPrincipal;
import sleppynavigators.studyupbackend.presentation.notification.dto.FcmTokenDeleteRequest;
import sleppynavigators.studyupbackend.presentation.notification.dto.FcmTokenRequest;
import sleppynavigators.studyupbackend.presentation.notification.dto.FcmTokenResponse;
import sleppynavigators.studyupbackend.presentation.notification.dto.TestNotificationRequest;
import sleppynavigators.studyupbackend.presentation.notification.dto.TestNotificationResponse;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification", description = "알림 관련 API")
public class FcmController {

    private final FcmTokenService fcmTokenService;
    private final FcmNotificationService fcmNotificationService;

    @PutMapping("/tokens")
    @Operation(summary = "FCM 토큰 등록/갱신", description = "FCM 토큰을 등록하거나 갱신합니다. 디바이스 ID가 이미 존재하는 경우 토큰을 갱신합니다.")
    public ResponseEntity<FcmTokenResponse> upsertToken(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody FcmTokenRequest request
    ) {

        FcmToken fcmToken = fcmTokenService.upsertToken(
                userPrincipal.userId(),
                request.token(),
                request.deviceId(),
                request.deviceType()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(FcmTokenResponse.fromEntity(fcmToken));
    }

    @DeleteMapping("/tokens")
    @Operation(summary = "FCM 토큰 삭제", description = "특정 디바이스의 FCM 토큰을 삭제합니다.")
    public ResponseEntity<Void> deleteToken(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody FcmTokenDeleteRequest request
    ) {
        fcmTokenService.deleteTokenByDeviceId(request.deviceId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/tokens/all")
    @Operation(summary = "FCM 토큰 전체 삭제", description = "사용자의 모든 FCM 토큰을 삭제합니다.")
    public ResponseEntity<Void> deleteAllTokens(
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        fcmTokenService.deleteAllTokensByUserId(userPrincipal.userId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/test")
    @Operation(
            summary = "테스트 알림 전송",
            description = "사용자의 모든 등록된 디바이스에 테스트 알림을 전송합니다. " +
                    "알림을 수신하려면 최소한 하나 이상의 FCM 토큰이 등록되어 있어야 합니다."
    )
    public ResponseEntity<List<TestNotificationResponse>> sendTestNotification(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody TestNotificationRequest request
    ) {
        List<String> messageIds = fcmNotificationService.sendTestNotification(
                userPrincipal.userId(),
                request.title(),
                request.body(),
                request.imageUrl(),
                request.data()
        );
        return ResponseEntity.ok(
                messageIds.stream()
                        .map(TestNotificationResponse::from)
                        .toList()
        );
    }
}
