package sleppynavigators.studyupbackend.domain.event;

public record UserLeaveEvent(String userName, Long groupId) implements SystemEvent {

    @Override
    public EventType getType() {
        return EventType.USER_LEAVE;
    }
}
