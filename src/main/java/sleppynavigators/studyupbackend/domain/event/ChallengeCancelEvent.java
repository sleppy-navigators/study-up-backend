package sleppynavigators.studyupbackend.domain.event;

public record ChallengeCancelEvent(String userName, String challengeName, Long groupId) implements SystemEvent {

    private static final String MESSAGE_FORMAT = "%s님이 '%s' 챌린지를 취소했습니다.";

    @Override
    public SystemEventType getType() {
        return SystemEventType.CHALLENGE_CANCEL;
    }

    @Override
    public Long getGroupId() {
        return groupId;
    }

    @Override
    public String generateMessage() {
        return String.format(MESSAGE_FORMAT, userName, challengeName);
    }
}
