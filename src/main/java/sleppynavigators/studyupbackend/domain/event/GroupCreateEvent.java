package sleppynavigators.studyupbackend.domain.event;

public record GroupCreateEvent(String userName, String groupName, Long groupId) implements SystemEvent {

    private static final String MESSAGE_FORMAT = "%s님이 '%s' 그룹을 생성했습니다.";

    @Override
    public SystemEventType getType() {
        return SystemEventType.GROUP_CREATE;
    }

    @Override
    public Long getGroupId() {
        return groupId;
    }

    @Override
    public String generateMessage() {
        return String.format(MESSAGE_FORMAT, userName, groupName);
    }
}
