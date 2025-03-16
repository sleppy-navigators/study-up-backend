package sleppynavigators.studyupbackend.domain.chat;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Document(collection = "chatMessages")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class ChatMessage {

    @Id
    private ObjectId id;

    @Indexed
    private Long senderId;

    @Indexed
    private Long groupId;

    private String content;

    private SenderType senderType;

    @CreatedDate
    private LocalDateTime createdAt;

    @Builder
    private ChatMessage(Long senderId, Long groupId, String content, SenderType senderType, LocalDateTime createdAt) {
        this.id = new ObjectId();
        this.senderId = senderId;
        this.groupId = groupId;
        this.content = content;
        this.senderType = senderType;
        this.createdAt = (createdAt != null) ? createdAt : LocalDateTime.now();
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
}
