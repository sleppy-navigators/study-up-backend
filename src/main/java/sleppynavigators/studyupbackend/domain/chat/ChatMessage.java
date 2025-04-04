package sleppynavigators.studyupbackend.domain.chat;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import sleppynavigators.studyupbackend.domain.common.TimeAuditBaseDocument;

@Getter
@Document(collection = "chatMessages")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class ChatMessage extends TimeAuditBaseDocument {

    @Id
    private ObjectId id;

    @Indexed
    private Long senderId;

    // Maybe we need a combined index on 'groupId' and 'createdAt'
    @Indexed
    private Long groupId;

    private String content;

    private SenderType senderType;

    @Builder(access = AccessLevel.PRIVATE)
    private ChatMessage(Long senderId, Long groupId, String content, SenderType senderType) {
        this.senderId = senderId;
        this.groupId = groupId;
        this.content = content;
        this.senderType = senderType;
    }

    public static ChatMessage fromUser(Long userId, Long groupId, String content) {
        return ChatMessage.builder()
                .senderId(userId)
                .groupId(groupId)
                .content(content)
                .senderType(SenderType.USER)
                .build();
    }

    public static ChatMessage fromBot(Long botId, Long groupId, String content) {
        return ChatMessage.builder()
                .senderId(botId)
                .groupId(groupId)
                .content(content)
                .senderType(SenderType.BOT)
                .build();
    }

    public boolean isBelongTo(Long groupId) {
        return this.groupId.equals(groupId);
    }
}
