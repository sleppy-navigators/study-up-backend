package sleppynavigators.studyupbackend.presentation.group.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import sleppynavigators.studyupbackend.domain.challenge.Challenge;
import sleppynavigators.studyupbackend.domain.challenge.Task;
import sleppynavigators.studyupbackend.presentation.challenge.dto.response.ChallengerDTO;
import sleppynavigators.studyupbackend.presentation.challenge.dto.response.TaskCertificationDTO;

@Schema(description = "그룹의 챌린지 목록 응답")
public record GroupChallengeListResponse(
        @Schema(description = "그룹 챌린지 목록")
        @NotNull @Valid List<GroupChallengeListItem> challenges) {

    @Schema(description = "그룹 챌린지")
    public record GroupChallengeListItem(
            @Schema(description = "챌린지 ID", example = "1")
            @NotNull Long id,

            @Schema(description = "챌린지 제목", example = "스터디 챌린지")
            @NotBlank String title,

            @Schema(description = "챌린지 마감일", example = "2023-10-01T10:00:00Z")
            @NotNull ZonedDateTime deadline,

            @Schema(description = "챌린지 설명", example = "아무튼 공부하는 스터디")
            String description,

            @Schema(description = "챌린지 완료 여부", example = "true")
            @NotNull Boolean isCompleted,

            @Schema(description = "챌린저 정보")
            @NotNull @Valid ChallengerDTO challengerDetail,

            @Schema(description = "최근 인증 정보")
            @Valid TaskCertificationDTO recentCertification) {

        public static GroupChallengeListItem fromEntity(Challenge challenge) {
            Task recentCertifiedTask = challenge.getRecentCertifiedTask();

            return new GroupChallengeListItem(
                    challenge.getId(),
                    challenge.getDetail().getTitle(),
                    challenge.getDetail().getDeadline().atZone(ZoneId.systemDefault()),
                    challenge.getDetail().getDescription(),
                    challenge.isCompleted(),
                    ChallengerDTO.fromEntity(challenge),
                    (recentCertifiedTask != null) ?
                            TaskCertificationDTO.fromEntity(recentCertifiedTask.getCertification())
                            : null
            );
        }
    }

    public static GroupChallengeListResponse fromEntities(List<Challenge> challenges) {
        return new GroupChallengeListResponse(
                challenges.stream()
                        .map(GroupChallengeListItem::fromEntity)
                        .toList()
        );
    }
}
