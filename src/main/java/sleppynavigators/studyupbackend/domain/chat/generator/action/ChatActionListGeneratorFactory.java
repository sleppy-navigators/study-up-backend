package sleppynavigators.studyupbackend.domain.chat.generator.action;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import sleppynavigators.studyupbackend.domain.event.Event;
import sleppynavigators.studyupbackend.domain.event.EventType;

@Component
public class ChatActionListGeneratorFactory {

    private final Map<EventType, ChatActionListGenerator<?>> generatorMap;
    private final ChatActionListGenerator<?> fallbackGenerator = new FallbackChatActionListGenerator();

    public ChatActionListGeneratorFactory(List<ChatActionListGenerator<? extends Event>> generators) {
        this.generatorMap = generators.stream()
                .collect(Collectors.toMap(
                        ChatActionListGenerator::supportedEventType,
                        generator -> generator));
    }

    @SuppressWarnings("unchecked")
    public <T extends Event> ChatActionListGenerator<T> get(T event) {
        ChatActionListGenerator<?> generator = generatorMap.get(event.getType());
        return (ChatActionListGenerator<T>) Objects.requireNonNullElse(generator, fallbackGenerator);
    }
}
