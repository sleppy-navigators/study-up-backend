package sleppynavigators.studyupbackend.domain.notification.generator;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import sleppynavigators.studyupbackend.domain.event.EventType;
import sleppynavigators.studyupbackend.domain.event.challenge.TaskCertifyEvent;
import sleppynavigators.studyupbackend.domain.notification.NotificationMessage;

public class TaskCertifyNotificationMessageGeneratorTest {

    private final TaskCertifyNotificationMessageGenerator generator = new TaskCertifyNotificationMessageGenerator();

    @Test
    void getEventType_ShouldReturnTaskCertify() {
        // when
        EventType eventType = generator.supportedEventType();

        // then
        assertThat(eventType).isEqualTo(EventType.TASK_CERTIFY);
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
        TaskCertifyEvent event = new TaskCertifyEvent(userName, challengeName, taskName, groupId, challengeId, taskId);

        // when
        NotificationMessage message = generator.generate(event);

        // then
        assertThat(message.title()).isEqualTo("테스크 인증 알림");
        assertThat(message.body()).isEqualTo("홍길동님이 '알고리즘 문제 풀기' 테스크를 인증했습니다. (알고리즘 끝장내기)");
    }
}
