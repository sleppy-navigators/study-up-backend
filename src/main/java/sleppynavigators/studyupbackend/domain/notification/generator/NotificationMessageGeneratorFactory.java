package sleppynavigators.studyupbackend.domain.notification.generator;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import sleppynavigators.studyupbackend.domain.event.EventType;
import sleppynavigators.studyupbackend.domain.event.NotificationEvent;

@Component
public class NotificationMessageGeneratorFactory {

    private final Map<EventType, NotificationMessageGenerator<?>> generatorMap;

    public NotificationMessageGeneratorFactory(
            List<NotificationMessageGenerator<? extends NotificationEvent>> generators) {
        this.generatorMap = generators.stream()
                .collect(Collectors.toMap(
                        NotificationMessageGenerator::supportedEventType,
                        generator -> generator));
    }

    @SuppressWarnings("unchecked")
    public <T extends NotificationEvent> NotificationMessageGenerator<T> get(T event) {
        NotificationMessageGenerator<?> generator = generatorMap.get(event.getType());

        if (generator == null) {
            throw new IllegalArgumentException("No generator found for event type: " + event.getType());
        }

        return (NotificationMessageGenerator<T>) generator;
    }
}
