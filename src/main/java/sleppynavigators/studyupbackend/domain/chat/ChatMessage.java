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

    @CreatedDate
    private LocalDateTime createdAt;

    @Builder
    public ChatMessage(Long senderId, Long groupId, String content) {
        this.id = new ObjectId();
        this.senderId = senderId;
        this.groupId = groupId;
        this.content = content;
    }
} 