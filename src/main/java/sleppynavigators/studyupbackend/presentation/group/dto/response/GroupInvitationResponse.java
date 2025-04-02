package sleppynavigators.studyupbackend.presentation.group.dto.response;

import jakarta.validation.constraints.NotBlank;
import org.jetbrains.annotations.NotNull;
import sleppynavigators.studyupbackend.domain.group.invitation.GroupInvitation;

public record GroupInvitationResponse(@NotNull Long groupId,
                                      @NotBlank String groupName,
                                      @NotBlank String groupDescription,
                                      String groupThumbnailUrl,
                                      @NotNull Long invitationId,
                                      @NotBlank String invitationKey,
                                      @NotNull Long inviterId) {

    public static GroupInvitationResponse fromEntity(GroupInvitation groupInvitation) {
        return new GroupInvitationResponse(
                groupInvitation.getGroup().getId(),
                groupInvitation.getGroup().getGroupDetail().name(),
                groupInvitation.getGroup().getGroupDetail().description(),
                groupInvitation.getGroup().getGroupDetail().thumbnailUrl(),
                groupInvitation.getId(),
                groupInvitation.getInvitationKey(),
                groupInvitation.getLastModifier());
    }
}
