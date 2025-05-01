package sleppynavigators.studyupbackend.presentation.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import sleppynavigators.studyupbackend.domain.challenge.Task;
import sleppynavigators.studyupbackend.domain.challenge.vo.TaskCertification;
import sleppynavigators.studyupbackend.presentation.challenge.dto.response.TaskChallengeDTO;
import sleppynavigators.studyupbackend.presentation.challenge.dto.response.TaskCertificationDTO;
import sleppynavigators.studyupbackend.presentation.challenge.dto.response.TaskGroupDTO;

@Schema(description = "유저의 테스크 목록 응답")
public record UserTaskListResponse(@NotNull @Valid List<UserTaskListItem> tasks) {

    @Schema(description = "유저 테스크")
    public record UserTaskListItem(
            @Schema(description = "테스크 ID", example = "1")
            @NotNull Long id,

            @Schema(description = "테스크 제목", example = "과제 제출하기")
            @NotBlank String title,

            @Schema(description = "테스크 마감일", example = "2023-10-01T10:00:00Z")
            @NotNull ZonedDateTime deadline,

            @Schema(description = "테스크 인증 정보")
            @Valid TaskCertificationDTO certification,

            @Schema(description = "테스크 챌린지 정보")
            @NotNull @Valid TaskChallengeDTO challengeDetail,

            @Schema(description = "테스크 그룹 정보")
            @NotNull @Valid TaskGroupDTO groupDetail) {

        public static UserTaskListItem fromEntity(Task task) {
            TaskCertification taskCertification = task.getCertification();

            return new UserTaskListItem(
                    task.getId(),
                    task.getDetail().getTitle(),
                    task.getDetail().getDeadline().atZone(ZoneId.systemDefault()),
                    (taskCertification.isCertified())
                            ? TaskCertificationDTO.fromEntity(task.getCertification())
                            : null,
                    TaskChallengeDTO.fromEntity(task),
                    TaskGroupDTO.fromEntity(task)
            );
        }
    }

    public static UserTaskListResponse fromEntities(List<Task> tasks) {
        return new UserTaskListResponse(
                tasks.stream()
                        .map(UserTaskListItem::fromEntity)
                        .toList()
        );
    }
}
