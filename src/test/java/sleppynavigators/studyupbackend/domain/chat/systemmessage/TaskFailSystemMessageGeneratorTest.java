package sleppynavigators.studyupbackend.domain.chat.systemmessage;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import sleppynavigators.studyupbackend.domain.event.EventType;
import sleppynavigators.studyupbackend.domain.event.TaskFailEvent;

public class TaskFailSystemMessageGeneratorTest {

    private final TaskFailSystemMessageGenerator generator = new TaskFailSystemMessageGenerator();

    @Test
    void getEventType_ShouldReturnTaskFail() {
        // when
        EventType eventType = generator.supportedEventType();

        // then
        assertThat(eventType).isEqualTo(EventType.TASK_FAIL);
    }

    @Test
    void generate_ShouldReturnFormattedMessage() {
        // given
        String userName = "홍길동";
        String challengeName = "알고리즘 끝장내기";
        String taskName = "알고리즘 문제 풀기";
        Long groupId = 1L;
        TaskFailEvent event = new TaskFailEvent(userName, challengeName, taskName, groupId);

        // when
        String message = generator.generate(event);

        // then
        assertThat(message).isEqualTo("홍길동님이 '알고리즘 문제 풀기' 테스크에 실패했습니다. (알고리즘 끝장내기)");
    }
}
