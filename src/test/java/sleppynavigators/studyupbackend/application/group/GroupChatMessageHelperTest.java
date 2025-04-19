package sleppynavigators.studyupbackend.application.group;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sleppynavigators.studyupbackend.common.ApplicationBaseTest;
import sleppynavigators.studyupbackend.common.support.GroupSupport;
import sleppynavigators.studyupbackend.common.support.UserSupport;
import sleppynavigators.studyupbackend.domain.chat.ChatMessage;
import sleppynavigators.studyupbackend.domain.group.Group;
import sleppynavigators.studyupbackend.domain.user.User;
import sleppynavigators.studyupbackend.presentation.group.dto.response.GroupDTO;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class GroupChatMessageHelperTest extends ApplicationBaseTest {

    @Autowired
    private UserSupport userSupport;

    @Autowired
    private GroupSupport groupSupport;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = userSupport.registerUserToDB();
    }

    @Test
    @DisplayName("그룹과 최신 챗 메시지를 조합할 수 있다.")
    void aggregateGroupAndChatMessage() {
        // given
        List<Group> groupList = IntStream.range(0, 5)
                .mapToObj((ignored) -> groupSupport.registerGroupToDB(List.of(testUser)))
                .toList();
        List<ChatMessage> chatMessageList = IntStream.range(0, 5)
                .mapToObj((idx) -> groupSupport
                        .registerChatMessagesToDB(groupList.get(idx), testUser,
                                List.of("test1 - " + idx, "test2 - " + idx)))
                .flatMap(Collection::stream)
                .toList();

        // when
        Map<Group, ChatMessage> actual = GroupChatMessageHelper
                .aggregateGroupWithFirstChatMessage(groupList, chatMessageList);

        // then
        for (Group group : groupList) {
            ChatMessage lastChatMessage = actual.get(group);
            assertThat(lastChatMessage).isNotNull();
            assertThat(lastChatMessage.getGroupId()).isEqualTo(group.getId());
            assertThat(lastChatMessage.getSenderId()).isEqualTo(testUser.getId());
            assertThat(lastChatMessage.getContent()).startsWith("test2");
        }
    }

    @Test
    @DisplayName("그룹과 최신 챗 메시지를 조합할 수 있다. - 챗 메시지가 없는 경우")
    void aggregateGroupAndChatMessageWithoutChatMessage() {
        // given
        List<Group> groupList = IntStream.range(0, 5)
                .mapToObj((ignored) -> groupSupport.registerGroupToDB(List.of(testUser)))
                .toList();

        // when, then
        assertThatThrownBy(() -> GroupChatMessageHelper
                .aggregateGroupWithFirstChatMessage(groupList, List.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No chat message found for group");
    }

    @Test
    @DisplayName("그룹과 최신 챗 메시지 맵을 DTO 리스트로 변환할 수 있다.")
    void convertGroupAndChatMessageToDTO() {
        // given
        List<Group> groupList = IntStream.range(0, 5)
                .mapToObj((ignored) -> groupSupport.registerGroupToDB(List.of(testUser)))
                .toList();
        List<ChatMessage> chatMessageList = IntStream.range(0, 5)
                .mapToObj((idx) -> groupSupport
                        .registerChatMessagesToDB(groupList.get(idx), testUser,
                                List.of("test1 - " + idx, "test2 - " + idx)))
                .flatMap(Collection::stream)
                .toList();

        Map<Group, ChatMessage> groupToLastChatMessage = GroupChatMessageHelper
                .aggregateGroupWithFirstChatMessage(groupList, chatMessageList);

        // when
        List<GroupDTO> actual = GroupChatMessageHelper
                .convertAndSortToGroupDTOs(groupToLastChatMessage, GroupSortType.NONE);

        // then
        assertThat(actual).hasSize(groupList.size());
    }

    @Test
    @DisplayName("그룹과 최신 챗 메시지 맵을 DTO 리스트로 변환할 수 있다. - 정렬")
    void convertGroupAndChatMessageToDTOWithSort() {
        // given
        List<Group> groupList = IntStream.range(0, 5)
                .mapToObj((ignored) -> groupSupport.registerGroupToDB(List.of(testUser)))
                .toList();
        List<ChatMessage> chatMessageList = IntStream.range(0, 5)
                .mapToObj((idx) -> groupSupport
                        .registerChatMessagesToDB(groupList.get(idx), testUser,
                                List.of("test1 - " + idx, "test2 - " + idx)))
                .flatMap(Collection::stream)
                .toList();

        Map<Group, ChatMessage> groupToLastChatMessage = GroupChatMessageHelper
                .aggregateGroupWithFirstChatMessage(groupList, chatMessageList);

        // when
        List<GroupDTO> actual = GroupChatMessageHelper
                .convertAndSortToGroupDTOs(groupToLastChatMessage, GroupSortType.LATEST_CHAT);

        // then
        assertThat(actual).hasSize(groupList.size());
        assertThat(actual).isSortedAccordingTo(Comparator.comparing(GroupDTO::id).reversed());
    }
}
