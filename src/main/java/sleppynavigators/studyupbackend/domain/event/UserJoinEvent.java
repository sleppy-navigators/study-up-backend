package sleppynavigators.studyupbackend.domain.event;

public record UserJoinEvent(String userName, Long groupId) implements SystemEvent {

    @Override
    public EventType getType() {
        return EventType.USER_JOIN;
    }

    @Override
    public Long getGroupId() {
        return groupId;
    }
}
