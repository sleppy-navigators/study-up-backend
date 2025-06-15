package sleppynavigators.studyupbackend.domain.notification.generator;

import org.springframework.stereotype.Component;
import sleppynavigators.studyupbackend.domain.event.EventType;
import sleppynavigators.studyupbackend.domain.event.challenge.TaskFailEvent;
import sleppynavigators.studyupbackend.domain.notification.NotificationMessage;

@Component
public class TaskFailNotificationMessageGenerator implements NotificationMessageGenerator<TaskFailEvent> {

    private static final String TITLE = "테스크 실패 알림";
    private static final String MESSAGE_FORMAT = "%s님이 '%s' 테스크에 실패했습니다. (%s)";

    @Override
    public NotificationMessage generate(TaskFailEvent event) {
        String body = String.format(MESSAGE_FORMAT, event.userName(), event.taskName(), event.challengeName());
        return new NotificationMessage(TITLE, body, null, null);
    }

    @Override
    public EventType supportedEventType() {
        return EventType.TASK_FAIL;
    }
}
