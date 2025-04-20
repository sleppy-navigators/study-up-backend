package sleppynavigators.studyupbackend.presentation.group.dto.response;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import sleppynavigators.studyupbackend.application.group.GroupWithLastChatMessage;
import sleppynavigators.studyupbackend.domain.chat.ChatMessage;
import sleppynavigators.studyupbackend.domain.group.Group;

import java.util.List;

public record GroupListResponse(@NotNull @Valid List<GroupListItem> groups) {

    public record GroupListItem(@NotNull Long id,
                                @NotBlank String name,
                                String thumbnailUrl,
                                @NotNull Integer numOfMembers,
                                @NotBlank String lastChatMessage) {

        public static GroupListItem fromEntity(GroupWithLastChatMessage groupWithLastChatMessage) {
            Group group = groupWithLastChatMessage.group();
            ChatMessage chatMessage = groupWithLastChatMessage.lastChatMessage();

            return new GroupListItem(
                    group.getId(),
                    group.getGroupDetail().getName(),
                    group.getGroupDetail().getThumbnailUrl(),
                    group.getNumOfMembers(),
                    chatMessage.getContent()
            );
        }
    }

    public static GroupListResponse fromEntities(List<GroupWithLastChatMessage> groups) {
        return new GroupListResponse(groups.stream()
                .map(GroupListItem::fromEntity)
                .toList());
    }
}
