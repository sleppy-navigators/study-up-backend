package sleppynavigators.studyupbackend.presentation.challenge.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import sleppynavigators.studyupbackend.domain.challenge.Task;

public record TaskChallengeDTO(@NotNull Long challengeId,
                               @NotBlank String challengeTitle,
                               @NotNull Boolean isCompleted) {

    public static TaskChallengeDTO fromEntity(Task task) {
        return new TaskChallengeDTO(
                task.getChallenge().getId(),
                task.getChallenge().getDetail().getTitle(),
                task.getChallenge().isCompleted());
    }
}
