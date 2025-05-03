package sleppynavigators.studyupbackend.application.chat;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sleppynavigators.studyupbackend.domain.chat.Bot;
import sleppynavigators.studyupbackend.domain.chat.ChatMessage;
import sleppynavigators.studyupbackend.domain.chat.systemmessage.SystemMessageGenerator;
import sleppynavigators.studyupbackend.domain.event.SystemEvent;
import sleppynavigators.studyupbackend.domain.group.Group;
import sleppynavigators.studyupbackend.domain.chat.systemmessage.SystemMessageGeneratorFactory;
import sleppynavigators.studyupbackend.domain.user.User;
import sleppynavigators.studyupbackend.exception.business.ChatMessageException;
import sleppynavigators.studyupbackend.exception.business.ForbiddenContentException;
import sleppynavigators.studyupbackend.exception.database.EntityNotFoundException;
import sleppynavigators.studyupbackend.infrastructure.chat.BotRepository;
import sleppynavigators.studyupbackend.infrastructure.chat.ChatMessageRepository;
import sleppynavigators.studyupbackend.infrastructure.group.GroupRepository;
import sleppynavigators.studyupbackend.infrastructure.user.UserRepository;
import sleppynavigators.studyupbackend.presentation.chat.dto.request.ChatMessageRequest;
import sleppynavigators.studyupbackend.presentation.chat.dto.response.ChatMessageResponse;
import sleppynavigators.studyupbackend.presentation.chat.dto.response.ChatMessageListResponse;
import sleppynavigators.studyupbackend.presentation.common.SuccessResponse;
import sleppynavigators.studyupbackend.presentation.chat.dto.request.ChatMessageSearch;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessageService {

    private static final String GROUP_DESTINATION = "/topic/group/%s";

    private final SystemMessageGeneratorFactory systemMessageGeneratorFactory;
    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatMessageRepository chatMessageRepository;
    private final GroupRepository groupRepository;
    private final BotRepository botRepository;
    private final UserRepository userRepository;

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
            throw new ChatMessageException("메시지 처리 중 오류가 발생했습니다", e);
        }
    }

    public <T extends SystemEvent> void sendSystemMessage(T event) {
        ChatMessage savedMessage = null;
        try {
            SystemMessageGenerator<T> systemMessageGenerator = systemMessageGeneratorFactory.get(event);
            String content = systemMessageGenerator.generate(event);

            Long groupId = event.getGroupId();
            Bot bot = botRepository.findByGroupId(groupId)
                    .orElseThrow(() -> new EntityNotFoundException("Bot not found - groupId: " + groupId));
            String destination = String.format(GROUP_DESTINATION, groupId);

            ChatMessage chatMessage = ChatMessage.fromBot(bot.getId(), groupId, content);

            savedMessage = chatMessageRepository.save(chatMessage);
            sendToWebSocket(destination, savedMessage);
        } catch (Exception e) {
            if (savedMessage != null) {
                chatMessageRepository.delete(savedMessage);
            }
            throw new ChatMessageException("시스템 메시지 처리 중 오류가 발생했습니다", e);
        }
    }

    private void sendToWebSocket(String destination, ChatMessage message) {
        ChatMessageResponse response = ChatMessageResponse.from(message);
        messagingTemplate.convertAndSend(destination, new SuccessResponse<>(response));
    }

    @Transactional(readOnly = true)
    public ChatMessageListResponse getMessages(Long userId, Long groupId, ChatMessageSearch search) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found - userId: " + userId));
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Group not found - groupId: " + groupId));

        if (!group.hasMember(user)) {
            throw new ForbiddenContentException(
                    "User cannot access this group - userId: " + userId + ", groupId: " + groupId);
        }

        Pageable pageable = PageRequest.of(search.pageNum().intValue(), search.pageSize());
        Page<ChatMessage> messages = chatMessageRepository.findGroupMessages(group.getId(), pageable);
        return ChatMessageListResponse.from(messages);
    }
}
