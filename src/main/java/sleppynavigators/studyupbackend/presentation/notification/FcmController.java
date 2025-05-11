package sleppynavigators.studyupbackend.presentation.notification;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sleppynavigators.studyupbackend.application.notification.FcmNotificationService;
import sleppynavigators.studyupbackend.application.notification.FcmTokenService;
import sleppynavigators.studyupbackend.domain.notification.FcmToken;
import sleppynavigators.studyupbackend.presentation.authentication.filter.UserPrincipal;
import sleppynavigators.studyupbackend.presentation.common.SuccessResponse;
import sleppynavigators.studyupbackend.presentation.notification.dto.request.FcmTokenRequest;
import sleppynavigators.studyupbackend.presentation.notification.dto.request.TestNotificationRequest;
import sleppynavigators.studyupbackend.presentation.notification.dto.request.TestNotificationResponse;
import sleppynavigators.studyupbackend.presentation.notification.dto.response.FcmTokenResponse;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Tag(name = "Notification", description = "알림 관련 API")
public class FcmController {

    private final FcmTokenService fcmTokenService;
    private final FcmNotificationService fcmNotificationService;

    @PutMapping("/tokens")
    @Operation(summary = "FCM 토큰 등록/갱신", description = "FCM 토큰을 등록하거나 갱신합니다. 디바이스 ID가 이미 존재하는 경우 토큰을 갱신합니다.")
    public ResponseEntity<SuccessResponse<FcmTokenResponse>> upsertToken(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody FcmTokenRequest request
    ) {
        FcmToken fcmToken = fcmTokenService.upsertToken(userPrincipal.userId(), request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SuccessResponse<>(FcmTokenResponse.fromEntity(fcmToken)));
    }

    @DeleteMapping("/tokens")
    @Operation(
            summary = "FCM 토큰 삭제",
            description = "사용자의 FCM 토큰을 삭제합니다."
    )
    public ResponseEntity<SuccessResponse<Void>> deleteTokens(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(description = "삭제할 디바이스 ID. 제공되지 않으면 모든 토큰 삭제")
            @RequestParam(required = false) String deviceId
    ) {
        fcmTokenService.deleteTokens(userPrincipal.userId(), deviceId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/test")
    @Operation(
            summary = "테스트 알림 전송",
            description = "사용자의 모든 등록된 디바이스에 테스트 알림을 전송합니다. " +
                    "알림을 수신하려면 최소한 하나 이상의 FCM 토큰이 등록되어 있어야 합니다."
    )
    public ResponseEntity<SuccessResponse<List<TestNotificationResponse>>> sendTestNotification(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody TestNotificationRequest request
    ) {
        List<String> messageIds = fcmNotificationService.sendTestNotification(userPrincipal.userId(), request);
        return ResponseEntity.ok(
                new SuccessResponse<>(
                        messageIds.stream()
                                .map(TestNotificationResponse::from)
                                .toList()
                )
        );
    }
}
