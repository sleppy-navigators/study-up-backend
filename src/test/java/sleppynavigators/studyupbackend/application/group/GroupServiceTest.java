package sleppynavigators.studyupbackend.application.group;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.verify;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import sleppynavigators.studyupbackend.application.event.GroupEventListener;
import sleppynavigators.studyupbackend.application.event.SystemMessageEventListener;
import sleppynavigators.studyupbackend.common.ApplicationBaseTest;
import sleppynavigators.studyupbackend.common.support.BotSupport;
import sleppynavigators.studyupbackend.common.support.GroupSupport;
import sleppynavigators.studyupbackend.common.support.UserSupport;
import sleppynavigators.studyupbackend.domain.chat.Bot;
import sleppynavigators.studyupbackend.domain.event.group.GroupCreateEvent;
import sleppynavigators.studyupbackend.domain.event.group.UserJoinEvent;
import sleppynavigators.studyupbackend.domain.event.group.UserLeaveEvent;
import sleppynavigators.studyupbackend.domain.group.Group;
import sleppynavigators.studyupbackend.domain.group.invitation.GroupInvitation;
import sleppynavigators.studyupbackend.domain.user.User;
import sleppynavigators.studyupbackend.presentation.group.dto.request.GroupCreationRequest;
import sleppynavigators.studyupbackend.presentation.group.dto.request.GroupInvitationAcceptRequest;
import sleppynavigators.studyupbackend.presentation.group.dto.response.GroupResponse;

@DisplayName("GroupService 테스트")
class GroupServiceTest extends ApplicationBaseTest {

    @Autowired
    private GroupService groupService;

    @Autowired
    private UserSupport userSupport;

    @Autowired
    private GroupSupport groupSupport;

    @Autowired
    private BotSupport botSupport;

    @MockitoSpyBean
    private SystemMessageEventListener systemEventListener;

    @MockitoSpyBean
    private GroupEventListener groupEventListener;

    private User testUser;

    private Group testGroup;

    private GroupInvitation testInvitation;

    @BeforeEach
    void setUp() {
        testUser = userSupport.registerUserToDB();
        testGroup = groupSupport.registerGroupToDB(List.of(testUser));
        Bot testBot = botSupport.registerBotToDB(testGroup);
        testInvitation = groupSupport.callToMakeInvitation(testGroup, testUser);
    }

    @Test
    @DisplayName("그룹 생성 시 GroupCreateEvent가 발행된다")
    void createGroup_PublishesGroupCreateEvent() {
        // given
        User creator = userSupport.registerUserToDB();
        GroupCreationRequest request = new GroupCreationRequest("스터디하기", "스터디 설명", null);

        clearInvocations(systemEventListener);
        clearInvocations(groupEventListener);

        // when
        GroupResponse response = groupService.createGroup(creator.getId(), request);

        // then
        verify(systemEventListener).handleSystemMessageEvent(any(GroupCreateEvent.class));
        verify(groupEventListener).handleGroupCreateEvent(any(GroupCreateEvent.class));
    }

    @Test
    @DisplayName("그룹 참여 시 UserJoinEvent가 발행된다")
    void acceptInvitation_PublishesUserJoinEvent() {
        // given
        User newUser = userSupport.registerUserToDB();
        GroupInvitationAcceptRequest request = new GroupInvitationAcceptRequest(testInvitation.getInvitationKey());

        clearInvocations(systemEventListener);

        // when
        groupService.acceptInvitation(newUser.getId(), testGroup.getId(), testInvitation.getId(), request);

        // then
        verify(systemEventListener).handleSystemMessageEvent(any(UserJoinEvent.class));
    }

    @Test
    @DisplayName("그룹 탈퇴 시 UserLeaveEvent가 발행된다")
    void leaveGroup_PublishesUserLeaveEvent() {
        // given
        User anotherMember = userSupport.registerUserToDB();
        Group groupToLeave = groupSupport.registerGroupToDB(List.of(testUser, anotherMember));
        Bot testBot = botSupport.registerBotToDB(groupToLeave);

        clearInvocations(systemEventListener);

        // when
        groupService.leaveGroup(anotherMember.getId(), groupToLeave.getId());

        // then
        verify(systemEventListener).handleSystemMessageEvent(any(UserLeaveEvent.class));
    }
}
