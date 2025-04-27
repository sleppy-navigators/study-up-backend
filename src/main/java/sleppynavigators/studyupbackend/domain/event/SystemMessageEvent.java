package sleppynavigators.studyupbackend.domain.event;

public interface SystemMessageEvent {

    EventType getType();

    Long getGroupId();
}
