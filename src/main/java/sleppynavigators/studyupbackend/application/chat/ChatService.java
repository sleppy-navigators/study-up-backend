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
import sleppynavigators.studyupbackend.presentation.common.SuccessCode;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatService {

    private final SimpMessageSendingOperations messagingTemplate;

    public void sendMessage(ChatMessageRequest request, String destination) {
        try {
            ChatMessageResponse response = ChatMessageResponse.builder()
                    .groupId(request.groupId())
                    .senderId(request.senderId())
                    .content(request.content())
                    .timestamp(LocalDateTime.now())
                    .build();
            messagingTemplate.convertAndSend(destination, new SuccessResponse<>(
                    SuccessCode.QUERY_OK.getCode(), SuccessCode.QUERY_OK.getDefaultMessage(), response));
            log.info("Message sent to destination {}: {}", destination, request.content());
        } catch (Exception e) {
            log.error("예상치 못한 메시지 처리 오류 - 대상: {}, 원인: {}", destination, e.getMessage(), e);
            throw new ChatMessageException("메시지 처리 중 오류가 발생했습니다", e);
        }
    }
}
