package sleppynavigators.studyupbackend.presentation.challenge.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import sleppynavigators.studyupbackend.domain.challenge.Challenge;

@Schema(description = "챌린지 응답")
public record ChallengeResponse(
        @Schema(description = "챌린지 ID", example = "1")
        @NotNull Long id,

        @Schema(description = "챌린지 제목", example = "매일 30분 독서하기")
        @NotBlank String title,

        @Schema(description = "챌린지 마감일", example = "2023-10-01T12:00:00Z")
        @NotNull ZonedDateTime deadline,

        @Schema(description = "챌린지 설명", example = "매일 30분 독서하기 챌린지")
        String description,

        @Schema(description = "챌린지 잔여 보증금", example = "10000")
        @NotNull Long deposit) {

    public static ChallengeResponse fromEntity(Challenge challenge) {
        return new ChallengeResponse(
                challenge.getId(),
                challenge.getDetail().getTitle(),
                challenge.getDeadline().atZone(ZoneId.systemDefault()),
                challenge.getDetail().getDescription(),
                challenge.getDeposit().getAmount());
    }
}
