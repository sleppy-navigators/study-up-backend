package sleppynavigators.studyupbackend.application.group;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import sleppynavigators.studyupbackend.domain.chat.ChatMessage;
import sleppynavigators.studyupbackend.domain.group.Group;

@Component
public class GroupChatMessageAggregator {

    public List<GroupWithLastChatMessage> aggregateWithLastChatMessage(
            List<Group> groups, List<ChatMessage> chatMessages, GroupSortType sortType) {
        List<GroupWithLastChatMessage> aggregated = groups.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        group -> chatMessages.stream()
                                .filter(message -> message.isBelongTo(group.getId()))
                                .max(Comparator.comparing(ChatMessage::getCreatedAt))
                                .orElseThrow(() -> new IllegalArgumentException(
                                        "No chat message found for group - groupId " + group.getId()))
                ))
                .entrySet().stream()
                .map(entry -> new GroupWithLastChatMessage(entry.getKey(), entry.getValue()))
                .toList();

        return switch (sortType) {
            case LATEST_CHAT -> aggregated.stream()
                    .sorted(Comparator.comparing(
                                    (GroupWithLastChatMessage group) -> group.lastChatMessage().getCreatedAt())
                            .reversed())
                    .toList();
            case NONE -> aggregated.stream()
                    .sorted(Comparator.comparing(
                            (GroupWithLastChatMessage group) -> group.group().getId()))
                    .toList();
        };
    }
}
