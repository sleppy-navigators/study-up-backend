package sleppynavigators.studyupbackend.application.group;

import static org.mockito.Mockito.verify;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import sleppynavigators.studyupbackend.application.event.SystemEventListener;
import sleppynavigators.studyupbackend.common.ApplicationBaseTest;
import sleppynavigators.studyupbackend.common.support.BotSupport;
import sleppynavigators.studyupbackend.common.support.GroupSupport;
import sleppynavigators.studyupbackend.common.support.UserSupport;
import sleppynavigators.studyupbackend.domain.bot.Bot;
import sleppynavigators.studyupbackend.domain.event.UserJoinEvent;
import sleppynavigators.studyupbackend.domain.event.UserLeaveEvent;
import sleppynavigators.studyupbackend.domain.group.Group;
import sleppynavigators.studyupbackend.domain.group.invitation.GroupInvitation;
import sleppynavigators.studyupbackend.domain.user.User;
import sleppynavigators.studyupbackend.presentation.group.dto.request.GroupInvitationAcceptRequest;

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
    private SystemEventListener systemEventListener;

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
    @DisplayName("그룹 참여 시 UserJoinEvent가 발행된다")
    void acceptInvitation_PublishesUserJoinEvent() {
        // given
        User newUser = userSupport.registerUserToDB();
        GroupInvitationAcceptRequest request = new GroupInvitationAcceptRequest(testInvitation.getInvitationKey());

        // when
        groupService.acceptInvitation(newUser.getId(), testGroup.getId(), testInvitation.getId(), request);

        // then
        verify(systemEventListener).handleSystemEvent(
                new UserJoinEvent(testUser.getUserProfile().username(), testGroup.getId())
        );
    }

    @Test
    @DisplayName("그룹 탈퇴 시 UserLeaveEvent가 발행된다")
    void leaveGroup_PublishesUserLeaveEvent() {
        // given
        User anotherMember = userSupport.registerUserToDB();
        Group groupToLeave = groupSupport.registerGroupToDB(List.of(testUser, anotherMember));
        Bot testBot = botSupport.registerBotToDB(groupToLeave);

        // when
        groupService.leaveGroup(anotherMember.getId(), groupToLeave.getId());

        // then
        verify(systemEventListener).handleSystemEvent(
                new UserLeaveEvent(testUser.getUserProfile().username(), groupToLeave.getId())
        );
    }
}
