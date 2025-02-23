package sleppynavigators.studyupbackend.application.chat;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sleppynavigators.studyupbackend.domain.chat.ChatMessage;
import sleppynavigators.studyupbackend.exception.business.ChatMessageException;
import sleppynavigators.studyupbackend.infrastructure.chat.ChatMessageRepository;
import sleppynavigators.studyupbackend.presentation.chat.dto.ChatMessageRequest;
import sleppynavigators.studyupbackend.presentation.chat.dto.ChatMessageResponse;
import sleppynavigators.studyupbackend.presentation.chat.dto.response.ChatMessageListResponse;
import sleppynavigators.studyupbackend.presentation.common.SuccessResponse;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessageService {

    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatMessageRepository chatMessageRepository;

    // TODO(@Jayon): 향후 카프카 도입하여 메시지 전송 로직 변경
    public void sendMessage(ChatMessageRequest request, String destination, Long senderId) {
        ChatMessage savedMessage = null;
        try {
            ChatMessage chatMessage = ChatMessage.builder()
                    .senderId(senderId)
                    .groupId(request.groupId())
                    .content(request.content())
                    .build();
            
            savedMessage = chatMessageRepository.save(chatMessage);

            ChatMessageResponse response = ChatMessageResponse.from(savedMessage);
            messagingTemplate.convertAndSend(destination, new SuccessResponse<>(response));
        } catch (Exception e) {
            if (savedMessage != null) {
                chatMessageRepository.delete(savedMessage);
            }
            throw new ChatMessageException("메시지 처리 중 오류가 발생했습니다: " + e);
        }
    }

    @Transactional(readOnly = true)
    public ChatMessageListResponse getMessages(Long groupId, Pageable pageable) {
        Page<ChatMessage> messages = chatMessageRepository.findByGroupIdOrderByCreatedAtDesc(groupId, pageable);
        return ChatMessageListResponse.from(messages);
    }
}
