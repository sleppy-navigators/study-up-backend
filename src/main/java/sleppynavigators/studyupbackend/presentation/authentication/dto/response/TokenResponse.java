package sleppynavigators.studyupbackend.presentation.authentication.dto.response;

import jakarta.validation.constraints.NotBlank;

public record TokenResponse(@NotBlank String accessToken,
                            @NotBlank String refreshToken) {
}
