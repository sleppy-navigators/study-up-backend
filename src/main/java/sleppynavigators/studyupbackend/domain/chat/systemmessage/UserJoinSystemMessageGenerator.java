package sleppynavigators.studyupbackend.domain.chat.systemmessage;

import org.springframework.stereotype.Component;
import sleppynavigators.studyupbackend.domain.event.EventType;
import sleppynavigators.studyupbackend.domain.event.UserJoinEvent;

@Component
public class UserJoinSystemMessageGenerator implements SystemMessageGenerator<UserJoinEvent> {
    private static final String MESSAGE_FORMAT = "%s님이 그룹에 참여했습니다.";
    
    @Override
    public String generate(UserJoinEvent event) {
        return String.format(MESSAGE_FORMAT, event.userName());
    }

    @Override
    public EventType supportedEventType() {
        return EventType.USER_JOIN;
    }
}
