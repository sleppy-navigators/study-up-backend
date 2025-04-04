package sleppynavigators.studyupbackend.domain.event;

public interface SystemEvent {

    SystemEventType getType();

    Long getGroupId();

    String generateMessage();
}
