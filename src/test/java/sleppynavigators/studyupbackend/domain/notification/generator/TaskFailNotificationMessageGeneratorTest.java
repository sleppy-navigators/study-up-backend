package sleppynavigators.studyupbackend.domain.notification.generator;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import sleppynavigators.studyupbackend.domain.event.EventType;
import sleppynavigators.studyupbackend.domain.event.challenge.TaskFailEvent;
import sleppynavigators.studyupbackend.domain.notification.NotificationMessage;

public class TaskFailNotificationMessageGeneratorTest {

    private final TaskFailNotificationMessageGenerator generator = new TaskFailNotificationMessageGenerator();

    @Test
    void getEventType_ShouldReturnTaskFail() {
        // when
        EventType eventType = generator.supportedEventType();

        // then
        assertThat(eventType).isEqualTo(EventType.TASK_FAIL);
    }

    @Test
    void generate_ShouldReturnNotificationMessage() {
        // given
        String userName = "홍길동";
        String challengeName = "알고리즘 끝장내기";
        String taskName = "알고리즘 문제 풀기";
        Long groupId = 1L;
        Long challengeId = 1L;
        Long taskId = 1L;
        TaskFailEvent event = new TaskFailEvent(userName, challengeName, taskName, groupId, challengeId, taskId);

        // when
        NotificationMessage message = generator.generate(event);

        // then
        assertThat(message.title()).isEqualTo("테스크 실패 알림");
        assertThat(message.body()).isEqualTo("홍길동님이 '알고리즘 문제 풀기' 테스크에 실패했습니다. (알고리즘 끝장내기)");
    }
}
