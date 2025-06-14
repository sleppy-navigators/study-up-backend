package sleppynavigators.studyupbackend.domain.chat;

import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import sleppynavigators.studyupbackend.domain.chat.action.ChatAction;
import sleppynavigators.studyupbackend.domain.common.TimeAuditBaseDocument;

@Getter
@Document(collection = "chatMessages")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class ChatMessage extends TimeAuditBaseDocument {

    @Indexed
    private Long senderId;

    // Maybe we need a combined index on 'groupId' and 'createdAt'
    @Indexed
    private Long groupId;

    private String content;

    private SenderType senderType;

    private List<ChatAction> actionList;

    @Builder(access = AccessLevel.PRIVATE)
    private ChatMessage(Long senderId, Long groupId, String content, SenderType senderType,
                        List<ChatAction> actionList) {
        this.senderId = senderId;
        this.groupId = groupId;
        this.content = content;
        this.senderType = senderType;
        this.actionList = actionList;
    }

    public static ChatMessage fromUser(Long userId, Long groupId, String content) {
        return ChatMessage.builder()
                .senderId(userId)
                .groupId(groupId)
                .content(content)
                .senderType(SenderType.USER)
                .actionList(List.of())
                .build();
    }

    public static ChatMessage fromBot(Long botId, Long groupId, String content, List<ChatAction> actionList) {
        return ChatMessage.builder()
                .senderId(botId)
                .groupId(groupId)
                .content(content)
                .senderType(SenderType.BOT)
                .actionList(actionList)
                .build();
    }

    public boolean isBelongTo(Long groupId) {
        return this.groupId.equals(groupId);
    }
}
