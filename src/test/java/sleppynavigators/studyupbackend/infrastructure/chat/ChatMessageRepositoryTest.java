package sleppynavigators.studyupbackend.infrastructure.chat;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import sleppynavigators.studyupbackend.common.ApplicationBaseTest;
import sleppynavigators.studyupbackend.domain.chat.ChatMessage;

@DisplayName("ChatMessageRepository 테스트")
class ChatMessageRepositoryTest extends ApplicationBaseTest {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Test
    @DisplayName("채팅 메시지를 저장하고 조회할 수 있다")
    void saveChatMessage() {
        // given
        ChatMessage chatMessage = ChatMessage.fromUser(1L, 1L, "테스트 메시지");

        // when
        ChatMessage savedMessage = chatMessageRepository.save(chatMessage);

        // then
        assertThat(savedMessage.getId()).isNotNull();
        assertThat(savedMessage.getSenderId()).isEqualTo(chatMessage.getSenderId());
        assertThat(savedMessage.getGroupId()).isEqualTo(chatMessage.getGroupId());
        assertThat(savedMessage.getContent()).isEqualTo(chatMessage.getContent());
        assertThat(savedMessage.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("그룹별로 최신 메시지부터 조회할 수 있다")
    void findByGroupIdOrderByCreatedAtDescLatest() {
        // given
        Long groupId = 1L;
        for (int i = 1; i <= 5; i++) {
            chatMessageRepository.save(ChatMessage.fromUser(1L, groupId, "테스트 메시지 " + i));
        }

        // when
        Page<ChatMessage> messages = chatMessageRepository
                .findGroupMessages(groupId, PageRequest.of(0, 10));

        // then
        assertThat(messages).isNotEmpty();
        assertThat(messages.getContent()).hasSize(5);
        assertThat(messages.getContent().get(0).getContent()).isEqualTo("테스트 메시지 5");
    }
}
