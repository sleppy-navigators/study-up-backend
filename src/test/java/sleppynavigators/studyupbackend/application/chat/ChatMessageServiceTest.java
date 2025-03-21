package sleppynavigators.studyupbackend.application.chat;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.willThrow;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import sleppynavigators.studyupbackend.domain.chat.ChatMessage;
import sleppynavigators.studyupbackend.domain.event.Event;
import sleppynavigators.studyupbackend.exception.business.ChatMessageException;
import sleppynavigators.studyupbackend.infrastructure.chat.ChatMessageRepository;
import sleppynavigators.studyupbackend.presentation.chat.dto.ChatMessageRequest;
import sleppynavigators.studyupbackend.presentation.common.DatabaseCleaner;
import sleppynavigators.studyupbackend.presentation.common.SuccessResponse;
import sleppynavigators.studyupbackend.domain.bot.Bot;
import sleppynavigators.studyupbackend.infrastructure.bot.BotRepository;
import sleppynavigators.studyupbackend.domain.group.Group;
import sleppynavigators.studyupbackend.infrastructure.group.GroupRepository;
import sleppynavigators.studyupbackend.domain.user.User;
import sleppynavigators.studyupbackend.infrastructure.user.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("ChatService 통합 테스트")
class ChatMessageServiceTest {

    private static final Long AUTHENTICATED_USER_ID = 1L;

    @Autowired
    private ChatMessageService chatMessageService;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private BotRepository botRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @MockitoBean
    private SimpMessageSendingOperations messagingTemplate;

    @BeforeEach
    void setUp() {
        User creator = userRepository.save(new User("testUser", "test@test.com"));

        Group group = Group.builder()
            .name("testGroup")
            .description("테스트용 그룹")
            .thumbnailUrl("https://test.com")
            .creator(creator)
            .build();
        Group savedGroup = groupRepository.save(group);

        Bot bot = new Bot(savedGroup);
        botRepository.save(bot);
    }

    @TestConfiguration
    static class TestConfig {
        @Primary
        @Bean
        public SimpMessageSendingOperations messagingTemplate() {
            return Mockito.mock(SimpMessageSendingOperations.class);
        }
    }

    @AfterEach
    void tearDown() {
        databaseCleaner.execute();
    }

    @Test
    @DisplayName("메시지를 저장하고 WebSocket으로 전송한다")
    void sendMessage() {
        // given
        ChatMessageRequest request = new ChatMessageRequest(1L, "테스트 메시지");
        String destination = "/topic/group/1";
        Long senderId = 1L;

        // when
        chatMessageService.sendUserMessage(request, destination, senderId);

        // then
        verify(messagingTemplate).convertAndSend(eq(destination), any(SuccessResponse.class));

        ChatMessage savedMessage = chatMessageRepository.findByGroupIdOrderByCreatedAtDesc(
                        1L,
                        PageRequest.of(0, 1)
                )
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
        String username = "testUser";
        String destination = "/topic/group/1";

        // when
        chatMessageService.sendSystemMessage(groupId, Event.USER_JOIN, username);

        // then
        verify(messagingTemplate).convertAndSend(eq(destination), any(SuccessResponse.class));

        ChatMessage savedMessage = chatMessageRepository.findByGroupIdOrderByCreatedAtDesc(
                groupId,
                PageRequest.of(0, 1)
            )
            .getContent()
            .get(0);

        assertThat(savedMessage.getContent()).isEqualTo("testUser님이 그룹에 참여했습니다.");
    }

    @Test
    @DisplayName("시스템 메시지 전송 실패 시 ChatMessageException이 발생한다")
    void sendSystemMessage_WhenDeliveryFails_ThrowsChatMessageException() {
        // given
        Long groupId = 1L;
        String username = "testUser";
        String destination = "/topic/group/1";

        willThrow(new MessageDeliveryException("Failed to deliver message"))
                .given(messagingTemplate)
                .convertAndSend(eq(destination), any(SuccessResponse.class));

        // when & then
        assertThatThrownBy(() -> chatMessageService.sendSystemMessage(groupId, Event.USER_JOIN, username))
                .isInstanceOf(ChatMessageException.class);

        assertThat(chatMessageRepository.findAll()).isEmpty();
    }
}
