package sleppynavigators.studyupbackend.presentation.group.dto.response;

import jakarta.validation.constraints.NotBlank;
import org.jetbrains.annotations.NotNull;
import sleppynavigators.studyupbackend.domain.group.invitation.GroupInvitation;

public record GroupInvitationResponse(@NotNull Long invitationId,
                                      @NotBlank String invitationKey) {

    public static GroupInvitationResponse fromEntity(GroupInvitation groupInvitation) {
        return new GroupInvitationResponse(groupInvitation.getId(), groupInvitation.getInvitationKey());
    }
}
