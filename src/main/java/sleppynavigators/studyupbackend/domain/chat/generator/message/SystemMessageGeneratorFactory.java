package sleppynavigators.studyupbackend.domain.chat.generator.message;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import sleppynavigators.studyupbackend.domain.event.EventType;
import sleppynavigators.studyupbackend.domain.event.SystemMessageEvent;

@Component
public class SystemMessageGeneratorFactory {

    private final Map<EventType, SystemMessageGenerator<?>> generatorMap;

    public SystemMessageGeneratorFactory(List<SystemMessageGenerator<? extends SystemMessageEvent>> generators) {
        this.generatorMap = generators.stream()
                .collect(Collectors.toMap(
                        SystemMessageGenerator::supportedEventType,
                        generator -> generator));
    }

    @SuppressWarnings("unchecked")
    public <T extends SystemMessageEvent> SystemMessageGenerator<T> get(T event) {
        SystemMessageGenerator<?> generator = generatorMap.get(event.getType());

        if (generator == null) {
            throw new IllegalStateException("No generator found for event type: " + event.getType());
        }

        return (SystemMessageGenerator<T>) generator;
    }
}
