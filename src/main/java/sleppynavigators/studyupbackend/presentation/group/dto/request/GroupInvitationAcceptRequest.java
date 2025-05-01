package sleppynavigators.studyupbackend.presentation.group.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "그룹 초대 수락 요청")
public record GroupInvitationAcceptRequest(
        @Schema(description = "그룹 초대 키", example = "abc123")
        @NotBlank String invitationKey) {
}
