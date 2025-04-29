package sleppynavigators.studyupbackend.presentation.group.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.jetbrains.annotations.NotNull;
import sleppynavigators.studyupbackend.domain.group.invitation.GroupInvitation;

@Schema(description = "그룹 초대 응답")
public record GroupInvitationResponse(
        @Schema(description = "그룹 초대 ID", example = "1")
        @NotNull Long id,

        @Schema(description = "그룹 초대 키", example = "abc123")
        @NotBlank String invitationKey,

        @Schema(description = "초대자 ID", example = "1")
        @NotNull Long inviterId,

        @Schema(description = "그룹 ID", example = "1")
        @NotNull Long groupId) {

    public static GroupInvitationResponse fromEntity(GroupInvitation groupInvitation) {
        return new GroupInvitationResponse(
                groupInvitation.getId(),
                groupInvitation.getInvitationKey(),
                groupInvitation.getCreatedBy(),
                groupInvitation.getGroup().getId()
        );
    }
}
