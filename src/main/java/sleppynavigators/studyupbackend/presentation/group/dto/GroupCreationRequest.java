package sleppynavigators.studyupbackend.presentation.group.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record GroupCreationRequest(@NotBlank String name,
                                   @NotBlank String description,
                                   @Email String thumbnailUrl) {
}
