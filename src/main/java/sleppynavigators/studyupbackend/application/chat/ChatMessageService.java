package sleppynavigators.studyupbackend.application.chat;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sleppynavigators.studyupbackend.domain.bot.Bot;
import sleppynavigators.studyupbackend.domain.chat.ChatMessage;
import sleppynavigators.studyupbackend.domain.event.Event;
import sleppynavigators.studyupbackend.domain.chat.SystemMessageTemplate;
import sleppynavigators.studyupbackend.domain.group.Group;
import sleppynavigators.studyupbackend.exception.business.ChatMessageException;
import sleppynavigators.studyupbackend.exception.database.EntityNotFoundException;
import sleppynavigators.studyupbackend.infrastructure.bot.BotRepository;
import sleppynavigators.studyupbackend.infrastructure.chat.ChatMessageRepository;
import sleppynavigators.studyupbackend.infrastructure.group.GroupRepository;
import sleppynavigators.studyupbackend.presentation.chat.dto.ChatMessageRequest;
import sleppynavigators.studyupbackend.presentation.chat.dto.ChatMessageResponse;
import sleppynavigators.studyupbackend.presentation.chat.dto.response.ChatMessageListResponse;
import sleppynavigators.studyupbackend.presentation.common.SuccessResponse;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessageService {

    private static final String GROUP_DESTINATION = "/topic/group/%s";
    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatMessageRepository chatMessageRepository;
    private final GroupRepository groupRepository;
    private final BotRepository botRepository;

    public void sendUserMessage(ChatMessageRequest request, String destination, Long senderId) {
        ChatMessage savedMessage = null;
        try {
            ChatMessage chatMessage = ChatMessage.fromUser(senderId, request.groupId(), request.content());
            
            savedMessage = chatMessageRepository.save(chatMessage);
            sendToWebSocket(destination, savedMessage);
        } catch (Exception e) {
            if (savedMessage != null) {
                chatMessageRepository.delete(savedMessage);
            }
            throw new ChatMessageException("메시지 처리 중 오류가 발생했습니다: " + e);
        }
    }

    public void sendSystemMessage(Long groupId, Event event, String... args) {
        ChatMessage savedMessage = null;
        try {
            String content = SystemMessageTemplate.from(event).getMessage(args);
            Bot bot = botRepository.findByGroupId(groupId)
                    .orElseThrow(() -> new EntityNotFoundException("해당 그룹의 봇을 찾을 수 없습니다. groupId: " + groupId));
            String destination = String.format(GROUP_DESTINATION, groupId);
            
            ChatMessage chatMessage = ChatMessage.fromBot(bot.getId(), groupId, content);

            savedMessage = chatMessageRepository.save(chatMessage);
            sendToWebSocket(destination, savedMessage);
        } catch (Exception e) {
            if (savedMessage != null) {
                chatMessageRepository.delete(savedMessage);
            }
            throw new ChatMessageException("시스템 메시지 처리 중 오류가 발생했습니다: " + e);
        }
    }

    private void sendToWebSocket(String destination, ChatMessage message) {
        ChatMessageResponse response = ChatMessageResponse.from(message);
        messagingTemplate.convertAndSend(destination, new SuccessResponse<>(response));
    }

    @Transactional(readOnly = true)
    public ChatMessageListResponse getMessages(Long groupId, Pageable pageable) {
        Group group = groupRepository.findById(groupId).orElseThrow(EntityNotFoundException::new);
        Page<ChatMessage> messages = chatMessageRepository.findByGroupIdOrderByCreatedAtDesc(group.getId(), pageable);
        return ChatMessageListResponse.from(messages);
    }
}
