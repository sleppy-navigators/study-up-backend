package sleppynavigators.studyupbackend.domain.event;

public record UserJoinEvent(String userName, String joinMethod) implements SystemEvent {

    @Override
    public SystemEventType getType() {
        return SystemEventType.USER_JOIN;
    }
}
