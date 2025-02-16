package sleppynavigators.studyupbackend.application.chat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import sleppynavigators.studyupbackend.domain.chat.ChatMessage;
import sleppynavigators.studyupbackend.exception.business.ChatMessageException;
import sleppynavigators.studyupbackend.infrastructure.chat.ChatMessageRepository;
import sleppynavigators.studyupbackend.presentation.chat.dto.ChatMessageRequest;
import sleppynavigators.studyupbackend.presentation.common.DatabaseCleaner;
import sleppynavigators.studyupbackend.presentation.common.SuccessResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("ChatService 통합 테스트")
class ChatServiceTest {

    @Autowired
    private ChatService chatService;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @MockitoBean
    private SimpMessageSendingOperations messagingTemplate;

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
        chatService.sendMessage(request, destination, senderId);

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
    @DisplayName("WebSocket 전송 실패 시 메시지 저장도 롤백된다")
    void sendMessageFailWebSocket() {
        // given
        ChatMessageRequest request = new ChatMessageRequest(1L, "테스트 메시지");
        String destination = "/topic/group/1";
        Long senderId = 1L;

        doThrow(new RuntimeException("WebSocket 전송 실패"))
                .when(messagingTemplate)
                .convertAndSend(eq(destination), any(SuccessResponse.class));

        // when & then
        assertThatThrownBy(() -> chatService.sendMessage(request, destination, senderId))
                .isInstanceOf(ChatMessageException.class);

        // 메시지가 저장되지 않았는지 확인
        Page<ChatMessage> messages = chatMessageRepository.findByGroupIdOrderByCreatedAtDesc(
                1L,
                PageRequest.of(0, 1)
        );

        assertThat(messages.isEmpty()).isTrue();
    }
}
