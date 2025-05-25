package sleppynavigators.studyupbackend.domain.event;

public record UserLeaveEvent(String userName, Long groupId) implements SystemMessageEvent {

    @Override
    public EventType getType() {
        return EventType.USER_LEAVE;
    }

    @Override
    public Long getGroupId() {
        return groupId;
    }
}
