package sleppynavigators.studyupbackend.domain.event;

import sleppynavigators.studyupbackend.domain.chat.SystemMessageTemplate;

public record UserJoinEvent(String userName, Long groupId) implements SystemEvent {

    @Override
    public SystemEventType getType() {
        return SystemEventType.USER_JOIN;
    }

    @Override
    public Long getGroupId() {
        return groupId;
    }

    @Override
    public String generateMessage(SystemMessageTemplate template) {
        return template.format(userName());
    }
}
