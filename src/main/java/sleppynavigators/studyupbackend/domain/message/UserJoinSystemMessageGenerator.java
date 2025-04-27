package sleppynavigators.studyupbackend.domain.message;

import sleppynavigators.studyupbackend.domain.event.EventType;
import sleppynavigators.studyupbackend.domain.event.UserJoinEvent;

public class UserJoinSystemMessageGenerator implements SystemMessageGenerator<UserJoinEvent> {
    private static final String MESSAGE_FORMAT = "%s님이 그룹에 참여했습니다.";
    
    @Override
    public String generate(UserJoinEvent event) {
        return String.format(MESSAGE_FORMAT, event.userName());
    }
    
    @Override
    public boolean supports(UserJoinEvent event) {
        return event.getType() == EventType.USER_JOIN;
    }
}
