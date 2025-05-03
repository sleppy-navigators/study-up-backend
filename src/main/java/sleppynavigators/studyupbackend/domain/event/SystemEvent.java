package sleppynavigators.studyupbackend.domain.event;

public interface SystemEvent {

    EventType getType();

    Long getGroupId();
}
