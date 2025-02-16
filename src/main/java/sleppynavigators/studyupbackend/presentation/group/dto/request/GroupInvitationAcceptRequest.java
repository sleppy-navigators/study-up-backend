package sleppynavigators.studyupbackend.presentation.group.dto.request;

import jakarta.validation.constraints.NotBlank;

public record GroupInvitationAcceptRequest(@NotBlank String invitationKey) {
}
