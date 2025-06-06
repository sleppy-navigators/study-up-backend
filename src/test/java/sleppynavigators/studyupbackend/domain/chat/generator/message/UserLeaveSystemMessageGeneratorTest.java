package sleppynavigators.studyupbackend.domain.chat.generator.message;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import sleppynavigators.studyupbackend.domain.event.EventType;
import sleppynavigators.studyupbackend.domain.event.UserLeaveEvent;

class UserLeaveSystemMessageGeneratorTest {

    private final UserLeaveSystemMessageGenerator generator = new UserLeaveSystemMessageGenerator();

    @Test
    void getEventType_ShouldReturnUserLeave() {
        // when
        EventType eventType = generator.supportedEventType();

        // then
        assertThat(eventType).isEqualTo(EventType.USER_LEAVE);
    }

    @Test
    void generate_ShouldReturnFormattedMessage() {
        // given
        String userName = "홍길동";
        Long groupId = 1L;
        UserLeaveEvent event = new UserLeaveEvent(userName, groupId);

        // when
        String message = generator.generate(event);

        // then
        assertThat(message).isEqualTo("홍길동님이 그룹을 나갔습니다.");
    }
}
