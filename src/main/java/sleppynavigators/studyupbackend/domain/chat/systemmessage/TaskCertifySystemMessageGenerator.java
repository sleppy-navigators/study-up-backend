package sleppynavigators.studyupbackend.domain.chat.systemmessage;

import org.springframework.stereotype.Component;
import sleppynavigators.studyupbackend.domain.event.EventType;
import sleppynavigators.studyupbackend.domain.event.TaskCertifiedEvent;

@Component
public class TaskCertifySystemMessageGenerator implements SystemMessageGenerator<TaskCertifiedEvent> {
    private static final String MESSAGE_FORMAT = "%s님이 '%s' 테스크를 완료했습니다. (%s)";

    @Override
    public String generate(TaskCertifiedEvent event) {
        return String.format(MESSAGE_FORMAT, event.userName(), event.taskName(), event.challengeName());
    }

    @Override
    public EventType supportedEventType() {
        return EventType.TASK_CERTIFY;
    }
}
