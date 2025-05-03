package sleppynavigators.studyupbackend.application.group;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;
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

public class GroupChatMessageAggregatorTest extends ApplicationBaseTest {

    @Autowired
    private UserSupport userSupport;

    @Autowired
    private GroupSupport groupSupport;

    @Autowired
    private GroupChatMessageAggregator groupChatMessageAggregator;

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
                        .registerChatMessagesToDB(groupList.get(idx), testUser, List.of("test1", "test2")))
                .flatMap(Collection::stream)
                .toList();

        // when
        List<GroupWithLastChatMessage> actual = groupChatMessageAggregator
                .aggregateWithLastChatMessage(groupList, chatMessageList, GroupSortType.NONE);

        // then
        assertThat(actual).hasSize(5);
        assertThat(actual).allMatch(groupWithLastChatMessage ->
                groupWithLastChatMessage.lastChatMessage().getContent().startsWith("test2"));
        assertThat(actual).isSortedAccordingTo(
                Comparator.comparing(g -> g.group().getId()));
    }

    @Test
    @DisplayName("그룹과 최신 챗 메시지를 조합할 수 있다. - 정렬 기준 : 최신 챗 메시지")
    void aggregateGroupAndChatMessageWithLatestChatMessage() {
        // given
        List<Group> groupList = IntStream.range(0, 5)
                .mapToObj((ignored) -> groupSupport.registerGroupToDB(List.of(testUser)))
                .toList();
        List<ChatMessage> chatMessageList = IntStream.range(0, 5)
                .mapToObj((idx) -> groupSupport
                        .registerChatMessagesToDB(groupList.get(idx), testUser, List.of("test1", "test2")))
                .flatMap(Collection::stream)
                .toList();

        // when
        List<GroupWithLastChatMessage> actual = groupChatMessageAggregator
                .aggregateWithLastChatMessage(groupList, chatMessageList, GroupSortType.LATEST_CHAT);

        // then
        assertThat(actual).hasSize(5);
        assertThat(actual).allMatch(groupWithLastChatMessage ->
                groupWithLastChatMessage.lastChatMessage().getContent().equals("test2"));
        assertThat(actual).isSortedAccordingTo((g1, g2) ->
                g2.lastChatMessage().getCreatedAt()
                        .compareTo(g1.lastChatMessage().getCreatedAt()));
    }

    @Test
    @DisplayName("그룹과 챗 메시지를 조합할 수 있다. - 챗 메시지가 없는 경우")
    void aggregateGroupAndChatMessageWithoutChatMessage() {
        // given
        List<Group> groupList = IntStream.range(0, 5)
                .mapToObj((ignored) -> groupSupport.registerGroupToDB(List.of(testUser)))
                .toList();

        // when, then
        assertThatThrownBy(() -> groupChatMessageAggregator
                .aggregateWithLastChatMessage(groupList, List.of(), GroupSortType.NONE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No chat message found for group");
    }
}
