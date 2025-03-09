package sleppynavigators.studyupbackend.presentation.group.dto.response;

import java.util.List;
import sleppynavigators.studyupbackend.domain.group.Group;

public record GroupListResponse(List<GroupListItem> groups) {

    public record GroupListItem(Long id, String name, String thumbnailUrl, String lastSystemMessage) {

        public static GroupListItem fromEntity(Group group) {
            return new GroupListItem(
                    group.getId(),
                    group.getGroupDetail().name(),
                    group.getGroupDetail().thumbnailUrl(),
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
