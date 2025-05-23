package sleppynavigators.studyupbackend.domain.chat.systemmessage;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import sleppynavigators.studyupbackend.domain.event.EventType;
import sleppynavigators.studyupbackend.domain.event.SystemEvent;

@Component
public class SystemMessageGeneratorFactory {
    private final Map<EventType, SystemMessageGenerator<?>> generatorMap;

    public SystemMessageGeneratorFactory(List<SystemMessageGenerator<? extends SystemEvent>> generators) {
        this.generatorMap = generators.stream()
                .collect(Collectors.toMap(
                        SystemMessageGenerator::supportedEventType,
                        generator -> generator
                ));
    }

    @SuppressWarnings("unchecked")
    public <T extends SystemEvent> SystemMessageGenerator<T> get(T event) {
        SystemMessageGenerator<?> generator = generatorMap.get(event.getType());

        if (generator == null) {
            throw new IllegalArgumentException("No generator found for event type: " + event.getType());
        }

        return (SystemMessageGenerator<T>) generator;
    }
}
