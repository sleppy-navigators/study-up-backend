package sleppynavigators.studyupbackend.presentation.challenge.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import sleppynavigators.studyupbackend.domain.challenge.Challenge;
import sleppynavigators.studyupbackend.domain.group.Group;
import sleppynavigators.studyupbackend.domain.user.User;
import sleppynavigators.studyupbackend.exception.business.InvalidPayloadException;

public record ChallengeCreationRequest(@NotBlank String title,
                                       @NotNull LocalDateTime deadline,
                                       String description,
                                       @NotEmpty @Valid List<TaskRequest> tasks) {

    public record TaskRequest(@NotBlank String title, @NotNull LocalDateTime deadline) {
    }

    public Challenge toEntity(User owner, Group group) {
        try {
            Challenge challenge = Challenge.builder()
                    .owner(owner)
                    .group(group)
                    .title(title)
                    .deadline(deadline)
                    .description(description)
                    .build();
            for (TaskRequest task : tasks) {
                challenge.addTask(task.title(), task.deadline());
            }
            return challenge;
        } catch (IllegalArgumentException ignored) {
            throw new InvalidPayloadException();
        }
    }
}
