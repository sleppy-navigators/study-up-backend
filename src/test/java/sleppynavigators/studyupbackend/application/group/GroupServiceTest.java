package sleppynavigators.studyupbackend.application.group;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import sleppynavigators.studyupbackend.application.event.SystemEventListener;
import sleppynavigators.studyupbackend.domain.event.UserJoinEvent;
import sleppynavigators.studyupbackend.domain.event.UserLeaveEvent;
import sleppynavigators.studyupbackend.domain.group.Group;
import sleppynavigators.studyupbackend.domain.group.invitation.GroupInvitation;
import sleppynavigators.studyupbackend.domain.user.User;
import sleppynavigators.studyupbackend.infrastructure.group.GroupRepository;
import sleppynavigators.studyupbackend.infrastructure.group.invitation.GroupInvitationRepository;
import sleppynavigators.studyupbackend.infrastructure.user.UserRepository;
import sleppynavigators.studyupbackend.presentation.common.DatabaseCleaner;
import sleppynavigators.studyupbackend.presentation.group.dto.request.GroupCreationRequest;
import sleppynavigators.studyupbackend.presentation.group.dto.request.GroupInvitationAcceptRequest;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("GroupService 테스트")
class GroupServiceTest {

    @Autowired
    private GroupService groupService;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupInvitationRepository groupInvitationRepository;

    @MockitoSpyBean
    private SystemEventListener systemEventListener;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    private User testUser;
    private Group testGroup;
    private GroupInvitation testInvitation;

    @BeforeEach
    void setUp() {
        databaseCleaner.execute();
        testUser = userRepository.save(new User("testUser", "test@test.com"));
        
        GroupCreationRequest request = new GroupCreationRequest("testGroup", "description", null);
        testGroup = groupRepository.save(request.toEntity(testUser));
        testInvitation = groupInvitationRepository.save(new GroupInvitation(testGroup));
    }

    @Test
    @DisplayName("그룹 참여 시 UserJoinEvent가 발행된다")
    void acceptInvitation_PublishesUserJoinEvent() {
        // given
        User newUser = userRepository.save(new User("newUser", "new@test.com"));
        GroupInvitationAcceptRequest request = new GroupInvitationAcceptRequest(testInvitation.getInvitationKey());

        // when
        groupService.acceptInvitation(newUser.getId(), testGroup.getId(), testInvitation.getId(), request);

        // then
        verify(systemEventListener).handleSystemEvent(
            new UserJoinEvent("newUser", testGroup.getId())
        );
    }

    @Test
    @DisplayName("그룹 탈퇴 시 UserLeaveEvent가 발행된다")
    void leaveGroup_PublishesUserLeaveEvent() {
        // given
        User member = userRepository.save(new User("member", "member@test.com"));
        testGroup.addMember(member);
        groupRepository.save(testGroup);

        // when
        groupService.leaveGroup(member.getId(), testGroup.getId());

        // then
        verify(systemEventListener).handleSystemEvent(
            new UserLeaveEvent("member", testGroup.getId())
        );
    }
}
