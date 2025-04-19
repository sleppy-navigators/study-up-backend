package sleppynavigators.studyupbackend.presentation.group.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import sleppynavigators.studyupbackend.domain.chat.ChatMessage;
import sleppynavigators.studyupbackend.domain.group.Group;

public record GroupDTO(@NotNull Long id,
                       @NotBlank String name,
                       String thumbnailUrl,
                       @NotNull Integer numOfMembers,
                       @NotBlank String lastChatMessage) {

    public static GroupDTO fromEntity(Group group, ChatMessage chatMessage) {
        return new GroupDTO(
                group.getId(),
                group.getGroupDetail().getName(),
                group.getGroupDetail().getThumbnailUrl(),
                group.getNumOfMembers(),
                chatMessage.getContent()
        );
    }
}
