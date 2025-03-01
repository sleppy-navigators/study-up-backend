package sleppynavigators.studyupbackend.presentation.challenge.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import sleppynavigators.studyupbackend.domain.challenge.Challenge;
import sleppynavigators.studyupbackend.domain.group.GroupMember;

public record ChallengeCreationRequest(@NotBlank String title,
                                       @NotNull LocalDateTime deadline,
                                       String description,
                                       @NotEmpty List<TaskRequest> tasks) {

    public record TaskRequest(@NotBlank String title, @NotNull LocalDateTime deadline) {
    }

    public Challenge toEntity(GroupMember owner) {
        Challenge challenge = Challenge.builder()
                .owner(owner)
                .title(title)
                .deadline(deadline)
                .description(description)
                .build();
        tasks.forEach(task -> challenge.addTask(task.title(), task.deadline()));
        return challenge;
    }
}
