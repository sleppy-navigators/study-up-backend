package sleppynavigators.studyupbackend.domain.chat.generator.message;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import sleppynavigators.studyupbackend.domain.event.EventType;
import sleppynavigators.studyupbackend.domain.event.group.UserJoinEvent;

class UserJoinSystemMessageGeneratorTest {

    private final UserJoinSystemMessageGenerator generator = new UserJoinSystemMessageGenerator();

    @Test
    void getEventType_ShouldReturnUserJoin() {
        // when
        EventType eventType = generator.supportedEventType();

        // then
        assertThat(eventType).isEqualTo(EventType.USER_JOIN);
    }

    @Test
    void generate_ShouldReturnFormattedMessage() {
        // given
        String userName = "홍길동";
        Long groupId = 1L;
        Long userId = 1L;
        UserJoinEvent event = new UserJoinEvent(userName, groupId, userId);

        // when
        String message = generator.generate(event);

        // then
        assertThat(message).isEqualTo("홍길동님이 그룹에 참여했습니다.");
    }
}
