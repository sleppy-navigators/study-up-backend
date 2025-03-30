package sleppynavigators.studyupbackend.domain.event;

public record ChallengeCreateEvent(String userName, String challengeName) implements SystemEvent {

    @Override
    public SystemEventType getType() {
        return SystemEventType.CHALLENGE_CREATE;
    }
}
