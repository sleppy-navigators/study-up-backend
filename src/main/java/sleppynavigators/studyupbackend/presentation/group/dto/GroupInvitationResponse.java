package sleppynavigators.studyupbackend.presentation.group.dto;

import jakarta.validation.constraints.NotBlank;

public record GroupInvitationResponse(@NotBlank String invitationId) {
}
