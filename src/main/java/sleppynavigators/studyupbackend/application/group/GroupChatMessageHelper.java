package sleppynavigators.studyupbackend.application.group;

import sleppynavigators.studyupbackend.domain.chat.ChatMessage;
import sleppynavigators.studyupbackend.domain.group.Group;
import sleppynavigators.studyupbackend.presentation.group.dto.response.GroupDTO;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GroupChatMessageHelper {

    /**
     * Aggregates a list of groups with their latest chat message based on the creation date.
     *
     * @param groups       The list of groups to aggregate.
     * @param chatMessages The list of chat messages to filter.
     * @return A map where the key is the group and the value is the first matching chat message
     */
    public static Map<Group, ChatMessage> aggregateGroupWithFirstChatMessage(
            List<Group> groups, List<ChatMessage> chatMessages) {
        return groups.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        group -> chatMessages.stream()
                                .filter(message -> message.isBelongTo(group.getId()))
                                .max(Comparator.comparing(ChatMessage::getCreatedAt))
                                .orElseThrow(() -> new IllegalArgumentException(
                                        "No chat message found for group - groupId " + group.getId()))
                ));
    }

    /**
     * Converts a map of groups and their last chat messages to a list of GroupDTOs, sorted by the specified sort type.
     *
     * @param groupToLastChatMessage A map where the key is the group and the value is the last chat message
     * @param sortType               The type of sorting to apply
     * @return A list of GroupDTOs sorted by the specified sort type
     */
    public static List<GroupDTO> convertAndSortToGroupDTOs(
            Map<Group, ChatMessage> groupToLastChatMessage, GroupSortType sortType) {
        Comparator<Group> comparator = switch (sortType) {
            case LATEST_CHAT -> (group1, group2) -> {
                ChatMessage chatMessage1 = groupToLastChatMessage.get(group1);
                ChatMessage chatMessage2 = groupToLastChatMessage.get(group2);
                return chatMessage2.getCreatedAt().compareTo(chatMessage1.getCreatedAt());
            };
            case NONE -> Comparator.comparing((Group group) -> group.getId());
        };

        return groupToLastChatMessage.keySet().stream()
                .sorted(comparator)
                .map(group -> GroupDTO.fromEntity(group, groupToLastChatMessage.get(group)))
                .toList();
    }
}
