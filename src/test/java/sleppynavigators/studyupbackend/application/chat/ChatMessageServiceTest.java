package sleppynavigators.studyupbackend.application.chat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import sleppynavigators.studyupbackend.common.ApplicationBaseTest;
import sleppynavigators.studyupbackend.common.support.BotSupport;
import sleppynavigators.studyupbackend.common.support.GroupSupport;
import sleppynavigators.studyupbackend.common.support.UserSupport;
import sleppynavigators.studyupbackend.domain.chat.Bot;
import sleppynavigators.studyupbackend.domain.chat.ChatMessage;
import sleppynavigators.studyupbackend.domain.event.UserJoinEvent;
import sleppynavigators.studyupbackend.domain.group.Group;
import sleppynavigators.studyupbackend.domain.user.User;
import sleppynavigators.studyupbackend.exception.business.ChatMessageException;
import sleppynavigators.studyupbackend.infrastructure.chat.ChatMessageRepository;
import sleppynavigators.studyupbackend.presentation.chat.dto.request.ChatMessageRequest;
import sleppynavigators.studyupbackend.presentation.common.SuccessResponse;

@DisplayName("ChatService 통합 테스트")
class ChatMessageServiceTest extends ApplicationBaseTest {

    private static final Long AUTHENTICATED_USER_ID = 1L;

    @Autowired
    private ChatMessageService chatMessageService;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private UserSupport userSupport;

    @Autowired
    private GroupSupport groupSupport;

    @Autowired
    private BotSupport botSupport;

    @MockitoBean
    private SimpMessagingTemplate messagingTemplate;

    @BeforeEach
    void setUp() {
        User creator = userSupport.registerUserToDB();
        Group group = groupSupport.registerGroupToDB(List.of(creator));
        Bot bot = botSupport.registerBotToDB(group);
    }

    @Test
    @DisplayName("메시지를 저장하고 WebSocket으로 전송한다")
    void sendMessage() {
        // given
        ChatMessageRequest request = new ChatMessageRequest(1L, "테스트 메시지");
        String destination = "/topic/group/1";
        Long senderId = 1L;

        clearInvocations(messagingTemplate);

        // when
        chatMessageService.sendUserMessage(request, destination, senderId);

        // then
        verify(messagingTemplate).convertAndSend(eq(destination), any(SuccessResponse.class));

        ChatMessage savedMessage = chatMessageRepository
                .findGroupMessages(1L, PageRequest.of(0, 1))
                .getContent()
                .get(0);

        assertThat(savedMessage.getContent()).isEqualTo("테스트 메시지");
    }

    @Test
    @DisplayName("메시지 전송 실패 시 ChatMessageException이 발생한다")
    void sendMessage_WhenDeliveryFails_ThrowsChatMessageException() {
        // given
        String destination = "/topic/group/test";
        ChatMessageRequest request = ChatMessageRequest.builder()
                .groupId(1L)
                .content("test message")
                .build();

        willThrow(new MessageDeliveryException("Failed to deliver message"))
                .given(messagingTemplate)
                .convertAndSend(eq(destination), any(SuccessResponse.class));

        clearInvocations(messagingTemplate);

        // when & then
        assertThatThrownBy(() -> chatMessageService.sendUserMessage(request, destination, AUTHENTICATED_USER_ID))
                .isInstanceOf(ChatMessageException.class);

        assertThat(chatMessageRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("예상치 못한 오류 발생 시 ChatMessageException이 발생한다")
    void sendMessage_WhenUnexpectedError_ThrowsChatMessageException() {
        // given
        String destination = "/topic/group/test";
        ChatMessageRequest request = ChatMessageRequest.builder()
                .groupId(1L)
                .content("test message")
                .build();

        willThrow(new RuntimeException("Unexpected error"))
                .given(messagingTemplate)
                .convertAndSend(eq(destination), any(SuccessResponse.class));

        clearInvocations(messagingTemplate);

        // when & then
        assertThatThrownBy(() -> chatMessageService.sendUserMessage(request, destination, AUTHENTICATED_USER_ID))
                .isInstanceOf(ChatMessageException.class);

        assertThat(chatMessageRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("시스템 메시지를 저장하고 WebSocket으로 전송한다")
    void sendSystemMessage() {
        // given
        Long groupId = 1L;
        Long userId = 1L;
        String username = "testUser";
        UserJoinEvent event = new UserJoinEvent(username, groupId, userId);

        clearInvocations(messagingTemplate);

        // when
        chatMessageService.sendSystemMessage(event);

        // then
        verify(messagingTemplate).convertAndSend(
                eq(String.format("/topic/group/%d", groupId)),
                any(SuccessResponse.class)
        );

        ChatMessage savedMessage = chatMessageRepository
                .findGroupMessages(groupId, PageRequest.of(0, 1))
                .getContent()
                .get(0);

        assertThat(savedMessage.getContent()).isEqualTo("testUser님이 그룹에 참여했습니다.");
    }

    @Test
    @DisplayName("시스템 메시지 전송 실패 시 ChatMessageException이 발생한다")
    void sendSystemMessage_WhenDeliveryFails_ThrowsChatMessageException() {
        // given
        Long groupId = 1L;
        Long userId = 1L;
        String username = "testUser";
        UserJoinEvent event = new UserJoinEvent(username, groupId, userId);

        doThrow(new RuntimeException("메시지 전송 실패"))
                .when(messagingTemplate)
                .convertAndSend(eq(String.format("/topic/group/%d", groupId)), any(SuccessResponse.class));

        clearInvocations(messagingTemplate);

        // when & then
        assertThatThrownBy(() -> chatMessageService.sendSystemMessage(event))
                .isInstanceOf(ChatMessageException.class)
                .hasMessageContaining("메시지 처리 중 오류가 발생했습니다");
    }
}
