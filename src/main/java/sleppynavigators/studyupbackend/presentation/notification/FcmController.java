package sleppynavigators.studyupbackend.presentation.notification;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sleppynavigators.studyupbackend.application.notification.FcmTokenService;
import sleppynavigators.studyupbackend.domain.notification.FcmToken;
import sleppynavigators.studyupbackend.presentation.authentication.filter.UserPrincipal;
import sleppynavigators.studyupbackend.presentation.notification.dto.FcmTokenDeleteRequest;
import sleppynavigators.studyupbackend.presentation.notification.dto.FcmTokenRequest;
import sleppynavigators.studyupbackend.presentation.notification.dto.FcmTokenResponse;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification", description = "알림 관련 API")
public class FcmController {

    private final FcmTokenService fcmTokenService;

    @PostMapping("/tokens")
    @Operation(summary = "FCM 토큰 등록/갱신", description = "FCM 토큰을 등록하거나 갱신합니다. 디바이스 ID가 이미 존재하는 경우 토큰을 갱신합니다.")
    public ResponseEntity<FcmTokenResponse> registerToken(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody FcmTokenRequest request
    ) {

        FcmToken fcmToken = fcmTokenService.registerToken(
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
        fcmTokenService.deleteTokenByDeviceId(userPrincipal.userId(), request.deviceId());
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
}
