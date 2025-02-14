package sleppynavigators.studyupbackend.presentation.authentication.dto.request;

import jakarta.validation.constraints.NotBlank;

public record SignInRequest(@NotBlank String idToken) {
}
