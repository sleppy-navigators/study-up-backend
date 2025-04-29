package sleppynavigators.studyupbackend.presentation.challenge.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import sleppynavigators.studyupbackend.domain.challenge.Task;

@Schema(description = "테스크 그룹")
public record TaskGroupDTO(
        @Schema(description = "그룹 ID", example = "1")
        @NotNull Long groupId,

        @Schema(description = "그룹 이름", example = "웹 마스터 그룹")
        @NotBlank String groupName,

        @Schema(description = "그룹에 현재 참여 중인지 여부", example = "true")
        @NotNull Boolean currentlyJoined) {

    public static TaskGroupDTO fromEntity(Task task) {
        return new TaskGroupDTO(
                task.getChallenge().getGroup().getId(),
                task.getChallenge().getGroup().getGroupDetail().getName(),
                task.getChallenge().getGroup().hasMember(task.getChallenge().getOwner()));
    }
}
