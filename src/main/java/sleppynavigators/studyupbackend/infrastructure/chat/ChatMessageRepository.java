package sleppynavigators.studyupbackend.infrastructure.chat;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import sleppynavigators.studyupbackend.domain.chat.ChatMessage;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, ObjectId> {
    
    Page<ChatMessage> findByGroupIdOrderByCreatedAtDesc(Long groupId, Pageable pageable);
}
