package sleppynavigators.studyupbackend.domain.event;

public record UserLeaveEvent(String userName, String reason, String leaveTime) implements SystemEvent {

    @Override
    public SystemEventType getType() {
        return SystemEventType.USER_LEAVE;
    }
}
