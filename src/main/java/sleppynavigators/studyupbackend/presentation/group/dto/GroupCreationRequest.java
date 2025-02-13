package sleppynavigators.studyupbackend.presentation.group.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import sleppynavigators.studyupbackend.domain.group.Group;
import sleppynavigators.studyupbackend.domain.user.User;

public record GroupCreationRequest(@NotBlank String name,
                                   @NotBlank String description,
                                   @Email String thumbnailUrl) {

    public Group toEntity(User creator) {
        return Group.builder()
                .name(name)
                .description(description)
                .thumbnailUrl(thumbnailUrl)
                .creator(creator)
                .build();
    }
}
