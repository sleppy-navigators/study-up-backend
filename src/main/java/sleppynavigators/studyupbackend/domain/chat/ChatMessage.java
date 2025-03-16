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

    @Builder(builderMethodName = "userMessageBuilder")
    public ChatMessage(Long userId, Long groupId, String content) {
        this(userId, groupId, content, SenderType.USER, LocalDateTime.now());
    }

    @Builder(builderMethodName = "botMessageBuilder")
    public ChatMessage(Long botId, Long groupId, String content) {
        this(botId, groupId, content, SenderType.BOT, LocalDateTime.now());
    }

    private ChatMessage(
        Long senderId, 
        Long groupId, 
        String content, 
        SenderType senderType, 
        LocalDateTime createdAt
    ) {
        this.id = new ObjectId();
        this.senderId = senderId;
        this.groupId = groupId;
        this.content = content;
        this.senderType = senderType;
        this.createdAt = createdAt;
    }
}
