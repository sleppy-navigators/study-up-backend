package sleppynavigators.studyupbackend.presentation.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import sleppynavigators.studyupbackend.domain.user.User;

@Schema(description = "사용자 정보 응답")
public record UserResponse(
        @Schema(description = "사용자 ID", example = "1")
        @NotNull Long id,

        @Schema(description = "사용자 이름", example = "홍길동")
        @NotBlank String name,

        @Schema(description = "사용자 이메일", example = "user@example.com")
        @NotBlank String email) {

    public static UserResponse fromEntity(User user) {
        return new UserResponse(
                user.getId(),
                user.getUserProfile().getUsername(),
                user.getUserProfile().getEmail());
    }
}
