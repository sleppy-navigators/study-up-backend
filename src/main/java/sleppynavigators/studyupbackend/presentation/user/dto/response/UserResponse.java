package sleppynavigators.studyupbackend.presentation.user.dto.response;

import jakarta.validation.constraints.NotBlank;
import org.jetbrains.annotations.NotNull;
import sleppynavigators.studyupbackend.domain.user.User;

public record UserResponse(@NotNull Long id,
                           @NotBlank String name,
                           @NotBlank String email) {

    public static UserResponse fromEntity(User user) {
        return new UserResponse(
                user.getId(),
                user.getUserProfile().getUsername(),
                user.getUserProfile().getEmail());
    }
}
