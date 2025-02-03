package sleppynavigators.studyupbackend.presentation.authentication.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshRequest(@NotBlank String accessToken,
                             @NotBlank String refreshToken) {
}
