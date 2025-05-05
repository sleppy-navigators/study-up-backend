package sleppynavigators.studyupbackend.presentation.notification;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sleppynavigators.studyupbackend.application.notification.FcmTokenService;
import sleppynavigators.studyupbackend.domain.notification.FcmToken;
import sleppynavigators.studyupbackend.presentation.authentication.filter.UserPrincipal;
import sleppynavigators.studyupbackend.presentation.notification.dto.FcmTokenRequest;
import sleppynavigators.studyupbackend.presentation.notification.dto.FcmTokenResponse;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class FcmController {

    private final FcmTokenService fcmTokenService;

    @PostMapping("/tokens")
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
}
