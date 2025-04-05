package sleppynavigators.studyupbackend.presentation.group.dto.request;

import jakarta.validation.constraints.NotBlank;
import sleppynavigators.studyupbackend.domain.group.Group;
import sleppynavigators.studyupbackend.domain.user.User;
import sleppynavigators.studyupbackend.exception.business.InvalidPayloadException;

public record GroupCreationRequest(@NotBlank String name,
                                   @NotBlank String description,
                                   String thumbnailUrl) {

    public Group toEntity(User creator) {
        try {
            return Group.builder()
                    .name(name)
                    .description(description)
                    .thumbnailUrl(thumbnailUrl)
                    .creator(creator)
                    .build();
        } catch (IllegalArgumentException ex) {
            throw new InvalidPayloadException(ex);
        }
    }
}
