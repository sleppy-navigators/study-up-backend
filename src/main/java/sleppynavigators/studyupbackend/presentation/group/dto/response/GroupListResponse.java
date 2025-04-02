package sleppynavigators.studyupbackend.presentation.group.dto.response;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.function.Function;
import sleppynavigators.studyupbackend.domain.chat.ChatMessage;
import sleppynavigators.studyupbackend.domain.group.Group;

public record GroupListResponse(@NotNull @Valid List<GroupListItem> groups) {

    public record GroupListItem(@NotNull Long id,
                                @NotBlank String name,
                                String thumbnailUrl,
                                @NotNull Integer numOfMembers,
                                @NotBlank String lastChatMessage) {

        public static GroupListItem fromEntity(Group group, ChatMessage chatMessage) {
            return new GroupListItem(
                    group.getId(),
                    group.getGroupDetail().name(),
                    group.getGroupDetail().thumbnailUrl(),
                    group.getNumOfMembers(),
                    chatMessage.getContent()
            );
        }
    }

    public static GroupListResponse fromEntities(List<Group> groups, List<ChatMessage> chatMessages) {

        Function<Group, GroupListItem> aggregateToListItem = group -> {
            ChatMessage lastChatMessage = chatMessages.stream()
                    .filter(message -> message.isBelongTo(group.getId()))
                    .findFirst()
                    .orElseThrow(IllegalStateException::new);
            return GroupListItem.fromEntity(group, lastChatMessage);
        };

        return new GroupListResponse(
                groups.stream()
                        .map(aggregateToListItem)
                        .toList()
        );
    }
}
