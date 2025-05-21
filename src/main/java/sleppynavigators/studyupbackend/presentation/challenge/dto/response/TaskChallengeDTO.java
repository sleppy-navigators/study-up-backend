package sleppynavigators.studyupbackend.presentation.challenge.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import sleppynavigators.studyupbackend.domain.challenge.Task;

@Schema(description = "테스크 챌린지")
public record TaskChallengeDTO(
        @Schema(description = "챌린지 ID", example = "1")
        @NotNull Long challengeId,

        @Schema(description = "챌린지 제목", example = "챌린지 제목")
        @NotBlank String challengeTitle,

        @Schema(description = "챌린지 잔여 보증금", example = "10000")
        @NotNull Long challengeDeposit,

        @Schema(description = "챌린지 완료 여부", example = "true")
        @NotNull Boolean isCompleted) {

    public static TaskChallengeDTO fromEntity(Task task) {
        return new TaskChallengeDTO(
                task.getChallenge().getId(),
                task.getChallenge().getDetail().getTitle(),
                task.getChallenge().getDeposit().getAmount(),
                task.getChallenge().isCompleted());
    }
}
