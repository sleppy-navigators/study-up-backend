package sleppynavigators.studyupbackend.infrastructure.chat;

import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import sleppynavigators.studyupbackend.domain.chat.ChatMessage;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, ObjectId> {

    @Query(value = "{ 'groupId': ?0 }", sort = "{ 'createdAt': -1 }")
    Page<ChatMessage> findGroupMessages(Long groupId, Pageable pageable);

    @Aggregation(pipeline = {
            "{ $match: { 'groupId': { $in: ?0 } } }",
            "{ $sort: { 'createdAt': -1 } }",
            "{ $group: { '_id': '$groupId', 'latestMessage': { $first: '$$ROOT' } } }",
            "{ $replaceRoot: { 'newRoot': '$latestMessage' } }"
    })
    List<ChatMessage> findLatestGroupMessages(List<Long> groupIds);
}
