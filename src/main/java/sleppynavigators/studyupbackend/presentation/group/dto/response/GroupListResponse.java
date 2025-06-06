package sleppynavigators.studyupbackend.presentation.group.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.net.URL;
import java.util.List;
import sleppynavigators.studyupbackend.application.group.GroupWithLastChatMessage;
import sleppynavigators.studyupbackend.domain.chat.ChatMessage;
import sleppynavigators.studyupbackend.domain.group.Group;

@Schema(description = "그룹 목록 응답")
public record GroupListResponse(
        @Schema(description = "그룹 목록")
        @NotNull @Valid List<GroupListItem> groups) {


    public static GroupListResponse fromEntities(List<GroupWithLastChatMessage> groups) {
        return new GroupListResponse(groups.stream()
                .map(GroupListItem::fromEntity)
                .toList());
    }

    @Schema(description = "그룹 정보")
    public record GroupListItem(
            @Schema(description = "그룹 ID", example = "1")
            @NotNull Long id,

            @Schema(description = "그룹 이름", example = "스터디 그룹")
            @NotBlank String name,

            @Schema(description = "그룹 썸네일 URL", example = "https://example.com/thumbnail.jpg")
            URL thumbnailUrl,

            @Schema(description = "그룹 멤버 수", example = "10")
            @NotNull Integer memberCount,

            @Schema(description = "그룹 마지막 채팅 메시지", example = "테스터님이 그룹에 입장하셨습니다.")
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
}
