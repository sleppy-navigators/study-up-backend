package sleppynavigators.studyupbackend.presentation.group.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SimpleGroupResponse(@NotNull Long id,
                                  @NotBlank String name,
                                  @NotBlank String description,
                                  @Email String thumbnailUrl) {
}
