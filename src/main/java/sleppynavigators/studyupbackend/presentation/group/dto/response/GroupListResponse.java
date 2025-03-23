package sleppynavigators.studyupbackend.presentation.group.dto.response;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import sleppynavigators.studyupbackend.domain.group.Group;

public record GroupListResponse(@NotNull @Valid List<GroupListItem> groups) {

    public record GroupListItem(@NotNull Long id,
                                @NotBlank String name,
                                String thumbnailUrl,
                                @NotNull Integer numOfMembers,
                                @NotBlank String lastSystemMessage) {

        public static GroupListItem fromEntity(Group group) {
            return new GroupListItem(
                    group.getId(),
                    group.getGroupDetail().name(),
                    group.getGroupDetail().thumbnailUrl(),
                    group.getNumOfMembers(),
                    "누구누구님이 이런저런일을 했다고 하시네요. 1h"
            );
        }
    }

    public static GroupListResponse fromEntities(List<Group> groups) {
        return new GroupListResponse(
                groups.stream()
                        .map(GroupListItem::fromEntity)
                        .toList()
        );
    }
}
