package sleppynavigators.studyupbackend.domain.chat.systemmessage;

import org.springframework.stereotype.Component;
import sleppynavigators.studyupbackend.domain.event.EventType;
import sleppynavigators.studyupbackend.domain.event.TaskFailEvent;

@Component
public class TaskFailSystemMessageGenerator implements SystemMessageGenerator<TaskFailEvent> {

    private static final String MESSAGE_FORMAT = "%s님이 '%s' 테스크에 실패했습니다. (%s)";

    @Override
    public String generate(TaskFailEvent event) {
        return String.format(MESSAGE_FORMAT, event.userName(), event.taskName(), event.challengeName());
    }

    @Override
    public EventType supportedEventType() {
        return EventType.TASK_FAIL;
    }
}
