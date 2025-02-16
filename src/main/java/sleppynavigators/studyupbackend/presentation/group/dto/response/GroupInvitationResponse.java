package sleppynavigators.studyupbackend.presentation.group.dto.response;

import sleppynavigators.studyupbackend.domain.group.invitation.GroupInvitation;

public record GroupInvitationResponse(Long invitationId, String invitationKey) {

    public static GroupInvitationResponse fromEntity(GroupInvitation groupInvitation) {
        return new GroupInvitationResponse(groupInvitation.getId(), groupInvitation.getInvitationKey());
    }
}
