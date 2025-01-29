package sleppynavigators.studyupbackend.application.chat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import sleppynavigators.studyupbackend.presentation.chat.dto.ChatMessageRequest;
import sleppynavigators.studyupbackend.presentation.chat.dto.ChatMessageResponse;
import sleppynavigators.studyupbackend.presentation.chat.exception.ChatMessageException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("ChatService 테스트")
class ChatServiceTest {

    @Autowired
    private ChatService chatService;

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

    @Test
    @DisplayName("채팅 메시지를 성공적으로 전송한다")
    void sendMessage_Success() {
        // given
        Long groupId = 1L;
        Long senderId = 1L;
        String content = "test message";
        String destination = "/topic/group/" + groupId;

        ChatMessageRequest request = ChatMessageRequest.builder()
                .groupId(groupId)
                .senderId(senderId)
                .content(content)
                .build();

        // when
        chatService.sendMessage(request, destination);

        // then
        then(messagingTemplate)
                .should()
                .convertAndSend(eq(destination), any(ChatMessageResponse.class));
    }

    @Test
    @DisplayName("메시지 전송 실패 시 ChatMessageException이 발생한다")
    void sendMessage_WhenDeliveryFails_ThrowsChatMessageException() {
        // given
        String destination = "/topic/group/test";
        ChatMessageRequest request = ChatMessageRequest.builder()
                .groupId(1L)
                .senderId(1L)
                .content("test message")
                .build();

        willThrow(new MessageDeliveryException("Failed to deliver message"))
                .given(messagingTemplate)
                .convertAndSend(eq(destination), any(ChatMessageResponse.class));

        // when & then
        assertThatThrownBy(() -> chatService.sendMessage(request, destination))
                .isInstanceOf(ChatMessageException.class)
                .hasMessage("메시지 전송에 실패했습니다");
    }

    @Test
    @DisplayName("예상치 못한 오류 발생 시 ChatMessageException이 발생한다")
    void sendMessage_WhenUnexpectedError_ThrowsChatMessageException() {
        // given
        String destination = "/topic/group/test";
        ChatMessageRequest request = ChatMessageRequest.builder()
                .groupId(1L)
                .senderId(1L)
                .content("test message")
                .build();

        willThrow(new RuntimeException("Unexpected error"))
                .given(messagingTemplate)
                .convertAndSend(eq(destination), any(ChatMessageResponse.class));

        // when & then
        assertThatThrownBy(() -> chatService.sendMessage(request, destination))
                .isInstanceOf(ChatMessageException.class)
                .hasMessage("메시지 처리 중 오류가 발생했습니다");
    }
}