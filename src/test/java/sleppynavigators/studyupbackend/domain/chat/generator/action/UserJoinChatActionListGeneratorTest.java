package sleppynavigators.studyupbackend.domain.chat.generator.action;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import sleppynavigators.studyupbackend.domain.chat.action.ChatAction;
import sleppynavigators.studyupbackend.domain.chat.action.ChatActionType;
import sleppynavigators.studyupbackend.domain.event.EventType;
import sleppynavigators.studyupbackend.domain.event.group.UserJoinEvent;

public class UserJoinChatActionListGeneratorTest {

    private final UserJoinChatActionListGenerator generator = new UserJoinChatActionListGenerator();

    @Test
    void getEventType_ShouldReturnUserJoin() {
        // when
        EventType eventType = generator.supportedEventType();

        // then
        assertThat(eventType).isEqualTo(EventType.USER_JOIN);
    }

    @Test
    void generate_ShouldReturnChatActions() {
        // given
        String userName = "홍길동";
        Long groupId = 1L;
        Long userId = 1L;
        UserJoinEvent event = new UserJoinEvent(userName, groupId, userId);

        // when
        List<ChatAction> chatActions = generator.generate(event);

        // then
        assertThat(chatActions).hasSize(1);
        assertThat(chatActions.get(0).getType()).isEqualTo(ChatActionType.VIEW_MEMBER_PROFILE);
    }
}
