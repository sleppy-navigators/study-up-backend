package sleppynavigators.studyupbackend.presentation.group.dto.request;

import sleppynavigators.studyupbackend.domain.chat.ChatMessage;
import sleppynavigators.studyupbackend.domain.group.Group;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public record GroupSearch(GroupSortType sortBy) {

    public enum GroupSortType {
        LATEST,
        NONE,
    }

    private static final GroupSortType DEFAULT_SORT_BY = GroupSortType.NONE;

    public GroupSearch {
        if (sortBy == null) {
            sortBy = DEFAULT_SORT_BY;
        }
    }

    public List<Group> sort(List<Group> groups, List<ChatMessage> chatMessages) {

        Map<Group, ChatMessage> groupToLastChatMessage = groups.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        group -> chatMessages.stream()
                                .filter(message -> message.isBelongTo(group.getId()))
                                .findFirst()
                                .orElseThrow(() -> new IllegalArgumentException(
                                        "No chat message found for group - groupId " + group.getId()))
                ));

        return switch (sortBy) {
            case LATEST -> groups.stream()
                    .sorted((group1, group2) -> {
                        ChatMessage chatMessage1 = groupToLastChatMessage.get(group1);
                        ChatMessage chatMessage2 = groupToLastChatMessage.get(group2);
                        return chatMessage2.getCreatedAt().compareTo(chatMessage1.getCreatedAt());
                    })
                    .collect(Collectors.toList());
            case NONE -> groups;
        };
    }
}
