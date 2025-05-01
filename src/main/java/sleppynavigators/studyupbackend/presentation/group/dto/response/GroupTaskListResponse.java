package sleppynavigators.studyupbackend.presentation.group.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import sleppynavigators.studyupbackend.domain.challenge.Task;
import sleppynavigators.studyupbackend.domain.challenge.vo.TaskCertification;
import sleppynavigators.studyupbackend.presentation.challenge.dto.response.ChallengerDTO;
import sleppynavigators.studyupbackend.presentation.challenge.dto.response.TaskCertificationDTO;
import sleppynavigators.studyupbackend.presentation.challenge.dto.response.TaskChallengeDTO;

@Schema(description = "그룹의 테스크 목록 응답")
public record GroupTaskListResponse(
        @Schema(description = "그룹 테스크 목록")
        @NotNull @Valid List<GroupTaskListItem> tasks) {

    @Schema(description = "그룹 테스크")
    public record GroupTaskListItem(
            @Schema(description = "테스크 ID", example = "1")
            @NotNull Long id,

            @Schema(description = "테스크 제목", example = "과제 제출하기")
            @NotBlank String title,

            @Schema(description = "테스크 마감일", example = "2023-10-01T10:00:00Z")
            @NotNull ZonedDateTime deadline,

            @Schema(description = "테스크 챌린지 정보")
            @NotNull @Valid TaskChallengeDTO challengeDetail,

            @Schema(description = "테스크 챌린저 정보")
            @NotNull @Valid ChallengerDTO challengerDetail,

            @Schema(description = "테스크 인증 정보")
            @Valid TaskCertificationDTO certification) {

        public static GroupTaskListItem fromEntity(Task task) {
            TaskCertification taskCertification = task.getCertification();

            return new GroupTaskListItem(
                    task.getId(),
                    task.getDetail().getTitle(),
                    task.getDetail().getDeadline().atZone(ZoneId.systemDefault()),
                    TaskChallengeDTO.fromEntity(task),
                    ChallengerDTO.fromEntity(task.getChallenge()),
                    (taskCertification.isCertified()) ?
                            TaskCertificationDTO.fromEntity(taskCertification)
                            : null);
        }
    }

    public static GroupTaskListResponse fromEntities(List<Task> tasks) {
        return new GroupTaskListResponse(tasks.stream()
                .map(GroupTaskListItem::fromEntity)
                .toList());
    }
}
