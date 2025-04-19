package sleppynavigators.studyupbackend.presentation.challenge.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import sleppynavigators.studyupbackend.domain.challenge.Task;

public record TaskGroupDTO(@NotNull Long groupId,
                           @NotBlank String groupName,
                           @NotNull Boolean currentlyJoined) {

    public static TaskGroupDTO fromEntity(Task task) {
        return new TaskGroupDTO(
                task.getChallenge().getGroup().getId(),
                task.getChallenge().getGroup().getGroupDetail().getName(),
                task.getChallenge().getGroup().hasMember(task.getChallenge().getOwner()));
    }
}
