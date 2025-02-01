package sleppynavigators.studyupbackend.presentation.authentication.dto;

import jakarta.validation.constraints.NotBlank;

public record SignInRequest(@NotBlank String idToken) {
}
