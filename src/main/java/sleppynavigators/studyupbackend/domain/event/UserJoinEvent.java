package sleppynavigators.studyupbackend.domain.event;

import sleppynavigators.studyupbackend.domain.chat.SystemMessageTemplate;

public record UserJoinEvent(String userName, String joinMethod) implements SystemEvent {

    @Override
    public SystemEventType getType() {
        return SystemEventType.USER_JOIN;
    }

    @Override
    public String generateMessage(SystemMessageTemplate template) {
        return template.format(userName);
    }
}
