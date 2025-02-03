package sleppynavigators.studyupbackend.presentation.authentication;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sleppynavigators.studyupbackend.application.authentication.AuthProvider;
import sleppynavigators.studyupbackend.application.authentication.AuthService;
import sleppynavigators.studyupbackend.presentation.authentication.dto.RefreshRequest;
import sleppynavigators.studyupbackend.presentation.authentication.dto.SignInRequest;
import sleppynavigators.studyupbackend.presentation.authentication.dto.TokenResponse;
import sleppynavigators.studyupbackend.presentation.authentication.exception.InvalidCredentialException;
import sleppynavigators.studyupbackend.presentation.common.APIResponse;
import sleppynavigators.studyupbackend.presentation.common.APIResult;

@Tag(name = "Auth", description = "인증 관련 API")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthController {

    private final AuthService authService;

    @Valid
    @PostMapping("/sign-in")
    @Operation(summary = "로그인", description = "사용자 로그인합니다.")
    public APIResponse<TokenResponse> login(@RequestParam AuthProvider provider,
                                            @RequestBody @Valid SignInRequest signInRequest) {
        switch (provider) {
            case GOOGLE:
                TokenResponse response = authService.googleSignIn(signInRequest);
                return new APIResponse<>(APIResult.QUERY_OK, response);
            default:
                throw new InvalidCredentialException();
        }
    }

    @Valid
    @PostMapping("/refresh")
    @Operation(summary = "토큰 갱신", description = "사용자 토큰을 갱신합니다.")
    public APIResponse<TokenResponse> refresh(@RequestBody @Valid RefreshRequest refreshRequest) {
        TokenResponse response = authService.refresh(refreshRequest);
        return new APIResponse<>(APIResult.QUERY_OK, response);
    }
}
