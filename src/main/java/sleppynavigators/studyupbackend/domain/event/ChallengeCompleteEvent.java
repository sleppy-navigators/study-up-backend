package sleppynavigators.studyupbackend.domain.event;

public record ChallengeCompleteEvent(String userName, String challengeName) implements SystemEvent {

    @Override
    public SystemEventType getType() {
        return SystemEventType.CHALLENGE_COMPLETE;
    }
}
