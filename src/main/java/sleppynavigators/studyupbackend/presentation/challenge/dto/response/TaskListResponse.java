package sleppynavigators.studyupbackend.presentation.challenge.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import sleppynavigators.studyupbackend.domain.challenge.Task;
import sleppynavigators.studyupbackend.domain.challenge.vo.TaskCertification;

@Schema(description = "테스크 목록 응답")
public record TaskListResponse(
        @Schema(description = "테스크 목록")
        @NotNull @Valid List<TaskListItem> tasks) {

    @Schema(description = "테스크 정보")
    public record TaskListItem(
            @Schema(description = "테스크 ID", example = "1")
            @NotNull Long id,

            @Schema(description = "테스크 제목", example = "과제 제출하기")
            @NotBlank String title,

            @Schema(description = "테스크 마감일", example = "2023-10-01T10:00:00Z")
            @NotNull ZonedDateTime deadline,

            @Schema(description = "테스크 인증 정보")
            @Valid TaskCertificationDTO certification) {

        public static TaskListItem fromEntity(Task task) {
            TaskCertification certification = task.getCertification();

            return new TaskListItem(
                    task.getId(),
                    task.getDetail().getTitle(),
                    task.getDetail().getDeadline().atZone(ZoneId.systemDefault()),
                    (certification.isCertified())
                            ? TaskCertificationDTO.fromEntity(task.getCertification())
                            : null
            );
        }
    }

    public static TaskListResponse fromEntities(List<Task> tasks) {
        return new TaskListResponse(
                tasks.stream()
                        .map(TaskListItem::fromEntity)
                        .toList()
        );
    }
}
