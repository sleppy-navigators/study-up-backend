package sleppynavigators.studyupbackend.domain.chat.generator.message;

import org.springframework.stereotype.Component;
import sleppynavigators.studyupbackend.domain.event.EventType;
import sleppynavigators.studyupbackend.domain.event.TaskCertifyEvent;

@Component
public class TaskCertifySystemMessageGenerator implements SystemMessageGenerator<TaskCertifyEvent> {
    private static final String MESSAGE_FORMAT = "%s님이 '%s' 테스크를 완료했습니다. (%s)";

    @Override
    public String generate(TaskCertifyEvent event) {
        return String.format(MESSAGE_FORMAT, event.userName(), event.taskName(), event.challengeName());
    }

    @Override
    public EventType supportedEventType() {
        return EventType.TASK_CERTIFY;
    }
}
