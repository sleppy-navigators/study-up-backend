package sleppynavigators.studyupbackend.presentation.group.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import sleppynavigators.studyupbackend.domain.group.Group;

public record GroupListResponse(@NotNull List<GroupListItem> groups) {

    public record GroupListItem(@NotNull Long id,
                                @NotNull String name,
                                @Email String thumbnailUrl,
                                @NotBlank String lastSystemMessage) {

        public static GroupListItem fromEntity(Group group) {
            return new GroupListItem(
                    group.getId(),
                    group.getGroupDetail().name(),
                    group.getGroupDetail().thumbnailUrl(),
                    "누구누구님이 이런저런일을 했다고 하시네요. 1h"
            );
        }
    }
}
