package sleppynavigators.studyupbackend.presentation.challenge.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import sleppynavigators.studyupbackend.domain.challenge.Challenge;
import sleppynavigators.studyupbackend.domain.group.Group;
import sleppynavigators.studyupbackend.domain.user.User;
import sleppynavigators.studyupbackend.exception.business.InvalidPayloadException;

@Schema(description = "챌린지 생성 요청")
public record ChallengeCreationRequest(
        @Schema(description = "챌린지 제목", example = "스터디 챌린지")
        @NotBlank String title,

        @Schema(description = "챌린지 설명", example = "아무튼 공부하는 스터디")
        String description,

        @Schema(description = "챌린지 태스크 목록")
        @NotEmpty @Valid List<TaskRequest> tasks) {

    public Challenge toEntity(User owner, Group group) {
        try {
            Challenge challenge = Challenge.builder()
                    .owner(owner)
                    .group(group)
                    .title(title)
                    .description(description)
                    .build();
            for (TaskRequest task : tasks) {
                challenge.addTask(task.title(),
                        task.deadline().withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime());
            }
            return challenge;
        } catch (IllegalArgumentException ex) {
            throw new InvalidPayloadException(ex);
        }
    }

    @Schema(description = "챌린지 태스크 정보")
    public record TaskRequest(
            @Schema(description = "태스크 제목", example = "과제 제출하기")
            @NotBlank String title,

            @Schema(description = "태스크 마감일", example = "2023-10-01T10:00:00Z")
            @NotNull ZonedDateTime deadline) {
    }
}
