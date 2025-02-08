package sleppynavigators.studyupbackend.application.chat;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import sleppynavigators.studyupbackend.presentation.chat.dto.ChatMessageRequest;
import sleppynavigators.studyupbackend.presentation.chat.dto.ChatMessageResponse;
import sleppynavigators.studyupbackend.presentation.chat.exception.ChatMessageException;
import sleppynavigators.studyupbackend.presentation.common.SuccessResponse;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatService {

    private final SimpMessageSendingOperations messagingTemplate;

    public void sendMessage(ChatMessageRequest request, String destination, Long senderId) {
        try {
            ChatMessageResponse response = ChatMessageResponse.builder()
                    .groupId(request.groupId())
                    .senderId(senderId)
                    .content(request.content())
                    .timestamp(LocalDateTime.now())
                    .build();
            messagingTemplate.convertAndSend(destination, new SuccessResponse<>(response));
            log.info("Message sent to destination {}: {}", destination, request.content());
        } catch (Exception e) {
            throw new ChatMessageException("메시지 처리 중 오류가 발생했습니다", e);
        }
    }
}
