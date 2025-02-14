package sleppynavigators.studyupbackend.presentation.authentication.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RefreshRequest(@NotBlank String accessToken,
                             @NotBlank String refreshToken) {
}
