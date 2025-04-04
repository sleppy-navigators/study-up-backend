package sleppynavigators.studyupbackend.domain.event;

public record UserJoinEvent(String userName, Long groupId) implements SystemEvent {

    private static final String MESSAGE_FORMAT = "%s님이 그룹에 참여했습니다.";

    @Override
    public SystemEventType getType() {
        return SystemEventType.USER_JOIN;
    }

    @Override
    public Long getGroupId() {
        return groupId;
    }

    @Override
    public String generateMessage() {
        return String.format(MESSAGE_FORMAT, userName());
    }
}
