package sleppynavigators.studyupbackend.presentation.group.dto.response;

import jakarta.validation.constraints.NotBlank;
import org.jetbrains.annotations.NotNull;
import sleppynavigators.studyupbackend.domain.group.invitation.GroupInvitation;

public record GroupInvitationResponse(@NotNull Long id,
                                      @NotBlank String invitationKey,
                                      @NotNull Long inviterId,
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
