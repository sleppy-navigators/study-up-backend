package sleppynavigators.studyupbackend.presentation.group;

import static io.restassured.RestAssured.with;
import static io.restassured.http.Method.GET;
import static io.restassured.http.Method.POST;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.ExtractableResponse;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sleppynavigators.studyupbackend.application.challenge.ChallengeSortType;
import sleppynavigators.studyupbackend.application.challenge.TaskCertificationStatus;
import sleppynavigators.studyupbackend.application.group.GroupMemberSortType;
import sleppynavigators.studyupbackend.common.RestAssuredBaseTest;
import sleppynavigators.studyupbackend.common.support.AuthSupport;
import sleppynavigators.studyupbackend.common.support.ChallengeSupport;
import sleppynavigators.studyupbackend.common.support.GroupSupport;
import sleppynavigators.studyupbackend.common.support.UserSupport;
import sleppynavigators.studyupbackend.domain.challenge.Challenge;
import sleppynavigators.studyupbackend.domain.chat.ChatMessage;
import sleppynavigators.studyupbackend.domain.chat.action.HuntTaskChatAction;
import sleppynavigators.studyupbackend.domain.group.Group;
import sleppynavigators.studyupbackend.domain.group.invitation.GroupInvitation;
import sleppynavigators.studyupbackend.domain.user.User;
import sleppynavigators.studyupbackend.exception.ErrorCode;
import sleppynavigators.studyupbackend.infrastructure.chat.ChatMessageRepository;
import sleppynavigators.studyupbackend.infrastructure.group.GroupRepository;
import sleppynavigators.studyupbackend.infrastructure.group.invitation.GroupInvitationRepository;
import sleppynavigators.studyupbackend.presentation.challenge.dto.request.ChallengeCreationRequest;
import sleppynavigators.studyupbackend.presentation.challenge.dto.request.ChallengeCreationRequest.TaskRequest;
import sleppynavigators.studyupbackend.presentation.challenge.dto.request.TaskCertificationRequest;
import sleppynavigators.studyupbackend.presentation.challenge.dto.response.ChallengeResponse;
import sleppynavigators.studyupbackend.presentation.challenge.dto.response.ChallengerDTO;
import sleppynavigators.studyupbackend.presentation.challenge.dto.response.TaskChallengeDTO;
import sleppynavigators.studyupbackend.presentation.chat.dto.response.ChatMessageListResponse;
import sleppynavigators.studyupbackend.presentation.chat.dto.response.ChatMessageResponse;
import sleppynavigators.studyupbackend.presentation.chat.dto.response.ChatMessageResponse.ChatActionItem;
import sleppynavigators.studyupbackend.presentation.group.dto.request.GroupCreationRequest;
import sleppynavigators.studyupbackend.presentation.group.dto.request.GroupInvitationAcceptRequest;
import sleppynavigators.studyupbackend.presentation.group.dto.response.GroupChallengeListResponse;
import sleppynavigators.studyupbackend.presentation.group.dto.response.GroupChallengeListResponse.GroupChallengeListItem;
import sleppynavigators.studyupbackend.presentation.group.dto.response.GroupInvitationResponse;
import sleppynavigators.studyupbackend.presentation.group.dto.response.GroupMemberListResponse;
import sleppynavigators.studyupbackend.presentation.group.dto.response.GroupMemberListResponse.GroupMemberListItem;
import sleppynavigators.studyupbackend.presentation.group.dto.response.GroupResponse;
import sleppynavigators.studyupbackend.presentation.group.dto.response.GroupTaskListResponse;
import sleppynavigators.studyupbackend.presentation.group.dto.response.GroupTaskListResponse.GroupTaskListItem;

@DisplayName("GroupController API 테스트")
public class GroupControllerTest extends RestAssuredBaseTest {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupInvitationRepository groupInvitationRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private UserSupport userSupport;

    @Autowired
    private AuthSupport authSupport;

    @Autowired
    private GroupSupport groupSupport;

    @Autowired
    private ChallengeSupport challengeSupport;

    private User currentUser;

    @BeforeEach
    void setUp() {
        currentUser = userSupport.registerUserToDB();
        String bearerToken = authSupport.createBearerToken(currentUser);
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .addHeader("Authorization", bearerToken)
                .build();
    }

    @Test
    @DisplayName("사용자가 그룹 생성에 성공한다")
    void memberGroupCreation_Success() throws MalformedURLException {
        // given
        GroupCreationRequest request =
                new GroupCreationRequest("test group", "test description", new URL("https://test.com"));

        assert groupRepository.findAll().isEmpty();

        // when
        ExtractableResponse<?> response = with()
                .body(request)
                .when().request(POST, "/groups")
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(groupRepository.findAll()).hasSize(1);
        assertThat(response.jsonPath().getObject("data", GroupResponse.class))
                .satisfies(data -> {
                    assertThat(this.validator.validate(data)).isEmpty();
                    assertThat(data.name()).isEqualTo(request.name());
                    assertThat(data.description()).isEqualTo(request.description());
                    assertThat(data.thumbnailUrl()).isEqualTo(request.thumbnailUrl());
                });
    }

    @Test
    @DisplayName("그룹 조회에 성공한다")
    void memberGroupQuery_Success() {
        // given
        Group groupToQuery = groupSupport.callToMakeGroup(List.of(currentUser));

        // when
        ExtractableResponse<?> response = with()
                .when().request(GET, "/groups/{groupId}", groupToQuery.getId())
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(response.jsonPath().getObject("data", GroupResponse.class))
                .satisfies(data -> {
                    assertThat(this.validator.validate(data)).isEmpty();
                    assertThat(data.id()).isEqualTo(groupToQuery.getId());
                });
    }

    @Test
    @DisplayName("사용자가 그룹에서 탈퇴에 성공한다")
    void memberLeaveGroup_Success() {
        // given
        User anotherMember = userSupport.registerUserToDB();
        Group groupToLeave = groupSupport.callToMakeGroup(List.of(currentUser, anotherMember));

        // when
        ExtractableResponse<?> response = with()
                .when().request(POST, "/groups/{groupId}/leave", groupToLeave.getId())
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(groupRepository.findAll()).isNotEmpty();
        assertThat(Optional.ofNullable(response.jsonPath().get("data"))).isEmpty();
    }

    @Test
    @DisplayName("유일한 사용자가 그룹에서 탈퇴한다 (그룹이 삭제된다)")
    void uniqueMemberLeaveGroup_Success() {
        // given
        Group groupToLeave = groupSupport.callToMakeGroup(List.of(currentUser));

        // when
        ExtractableResponse<?> response = with()
                .when().request(POST, "/groups/{groupId}/leave", groupToLeave.getId())
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(groupRepository.findAll()).isEmpty();
        assertThat(Optional.ofNullable(response.jsonPath().get("data"))).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 그룹, 멤버에 탈퇴를 요청해도 정상 응답한다")
    void leaveGroup_NotFound() {
        // given
        Long groupIdThatDoesNotExist = 1L;
        assert groupRepository.findAll().isEmpty();

        // when
        ExtractableResponse<?> response = with()
                .when().request(POST, "/groups/{groupId}/leave", groupIdThatDoesNotExist)
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(Optional.ofNullable(response.jsonPath().get("data"))).isEmpty();
    }

    @Test
    @DisplayName("챌린저가 그룹 탈퇴를 요청하면 실패한다.")
    void challengerLeaveGroup_Fail() {
        // given
        Group groupToLeave = groupSupport.callToMakeGroup(List.of(currentUser));
        Challenge progressingChallenge = challengeSupport
                .callToMakeChallengesWithTasks(groupToLeave, 5, 3, currentUser);

        // when
        ExtractableResponse<?> response = with()
                .when().request(POST, "/groups/{groupId}/leave", groupToLeave.getId())
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_BAD_REQUEST);
        assertThat(response.jsonPath().getString("code")).isEqualTo(ErrorCode.ACTION_REQUIRED_BEFORE.getCode());
        assertThat(response.jsonPath().getString("message"))
                .contains("Challenger cannot leave the group");
    }

    @Test
    @DisplayName("그룹 초대를 생성한다")
    void createGroupInvitation_Success() {
        // given
        Group groupToInvite = groupSupport.callToMakeGroup(List.of(currentUser));

        // when
        ExtractableResponse<?> response = with()
                .when().request(POST, "/groups/{groupId}/invitations", groupToInvite.getId())
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(response.jsonPath().getObject("data", GroupInvitationResponse.class))
                .satisfies(data -> assertThat(this.validator.validate(data)).isEmpty());
    }

    @Test
    @DisplayName("존재하지 않는 그룹에 초대를 생성하면 오류로 응답한다")
    void createGroupInvitation_NotFound() {
        // given
        Long groupIdThatDoesNotExist = 1L;
        assert groupRepository.findAll().isEmpty();

        // when
        ExtractableResponse<?> response = with()
                .when().request(POST, "/groups/{groupId}/invitations", groupIdThatDoesNotExist)
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_NOT_FOUND);
        assertThat(response.jsonPath().getString("code")).isEqualTo(ErrorCode.ENTITY_NOT_FOUND.getCode());
        assertThat(response.jsonPath().getString("message"))
                .contains("Group member not found");
    }

    @Test
    @DisplayName("그룹 초대를 조회한다")
    void getGroupInvitations_Success() {
        // given
        Group groupToInvite = groupSupport.callToMakeGroup(List.of(currentUser));
        GroupInvitation invitation = groupSupport.callToMakeInvitation(groupToInvite, currentUser);

        // when
        ExtractableResponse<?> response = with()
                .when()
                .request(GET, "/groups/{groupId}/invitations/{invitationId}",
                        groupToInvite.getId(), invitation.getId())
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(response.jsonPath().getObject("data", GroupInvitationResponse.class))
                .satisfies(data -> {
                    assertThat(this.validator.validate(data)).isEmpty();
                    assertThat(data.groupId()).isEqualTo(groupToInvite.getId());
                });
    }

    @Test
    @DisplayName("존재하지 않는 그룹 초대를 조회하면 오류로 응답한다")
    void getGroupInvitations_NotFound() {
        // given
        Long groupIdThatDoesNotExist = 1L;
        Long invitationIdThatDoesNotExist = 1L;
        assert groupInvitationRepository.findAll().isEmpty();

        // when
        ExtractableResponse<?> response = with()
                .when().request(GET, "/groups/{groupId}/invitations/{invitationId}",
                        groupIdThatDoesNotExist, invitationIdThatDoesNotExist)
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_NOT_FOUND);
        assertThat(response.jsonPath().getString("code")).isEqualTo(ErrorCode.ENTITY_NOT_FOUND.getCode());
        assertThat(response.jsonPath().getString("message"))
                .contains("Invitation not found");
    }

    @Test
    @DisplayName("그룹 초대를 수락한다")
    void acceptGroupInvitation_Success() {
        // given
        Group groupToInvite = groupSupport.callToMakeGroup(List.of(currentUser));
        GroupInvitation invitation = groupSupport.callToMakeInvitation(groupToInvite, currentUser);

        GroupInvitationAcceptRequest request = new GroupInvitationAcceptRequest(invitation.getInvitationKey());

        // when
        ExtractableResponse<?> response = with()
                .body(request)
                .when()
                .request(POST, "/groups/{groupId}/invitations/{invitationId}/accept",
                        groupToInvite.getId(), invitation.getId())
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(response.jsonPath().getObject("data", GroupInvitationResponse.class))
                .satisfies(data -> assertThat(this.validator.validate(data)).isEmpty());
    }

    @Test
    @DisplayName("동일한 그룹에 대해 복수의 초대를 생성할 수 있다")
    void createGroupInvitation_Multiple_Success() {
        // given
        Group groupToInvite = groupSupport.callToMakeGroup(List.of(currentUser));
        GroupInvitation invitation1 = groupSupport.callToMakeInvitation(groupToInvite, currentUser);
        GroupInvitation invitation2 = groupSupport.callToMakeInvitation(groupToInvite, currentUser);

        // when
        ExtractableResponse<?> response1 = with()
                .when().request(POST, "/groups/{groupId}/invitations", groupToInvite.getId())
                .then()
                .log().all().extract();

        ExtractableResponse<?> response2 = with()
                .when().request(POST, "/groups/{groupId}/invitations", groupToInvite.getId())
                .then()
                .log().all().extract();

        // then
        assertThat(response1.statusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(response2.statusCode()).isEqualTo(HttpStatus.SC_OK);
    }

    @Test
    @DisplayName("존재하지 않는 그룹 초대를 수락하면 오류로 응답한다")
    void acceptGroupInvitation_NotFound() {
        // given
        Long groupIdThatDoesNotExist = 1L;
        Long invitationIdThatDoesNotExist = 1L;
        assert groupRepository.findAll().isEmpty();

        GroupInvitationAcceptRequest request = new GroupInvitationAcceptRequest("invitation_key_that_not_exist");

        // when
        ExtractableResponse<?> response = with()
                .body(request)
                .when().request(POST, "/groups/{groupId}/invitations/{invitationId}/accept",
                        groupIdThatDoesNotExist, invitationIdThatDoesNotExist)
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_NOT_FOUND);
        assertThat(response.jsonPath().getString("code")).isEqualTo(ErrorCode.ENTITY_NOT_FOUND.getCode());
        assertThat(response.jsonPath().getString("message"))
                .contains("Invitation not found");
    }

    @Test
    @DisplayName("그룹 초대 수락 시 초대 키가 일치하지 않으면 오류로 응답한다")
    void acceptGroupInvitation_InvalidKey() {
        // given
        Group groupToInvite = groupSupport.callToMakeGroup(List.of(currentUser));
        GroupInvitation invitation = groupSupport.callToMakeInvitation(groupToInvite, currentUser);

        GroupInvitationAcceptRequest request = new GroupInvitationAcceptRequest(
                invitation.getInvitationKey() + "make_it_invalid");

        // when
        ExtractableResponse<?> response = with()
                .body(request)
                .when()
                .request(POST, "/groups/{groupId}/invitations/{invitationId}/accept",
                        groupToInvite.getId(), invitation.getId())
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_BAD_REQUEST);
        assertThat(response.jsonPath().getString("code")).isEqualTo(ErrorCode.INVALID_PAYLOAD.getCode());
        assertThat(response.jsonPath().getString("message"))
                .contains("Invalid groupId or invitationKey");
    }

    @Test
    @DisplayName("그룹 초대 수락 시 주어진 그룹과 일치하지 않으면 오류로 응답한다")
    void acceptGroupInvitation_InvalidGroup() {
        // given
        Group groupToInvite = groupSupport.callToMakeGroup(List.of(currentUser));
        GroupInvitation invitation = groupSupport.callToMakeInvitation(groupToInvite, currentUser);

        GroupInvitationAcceptRequest request = new GroupInvitationAcceptRequest(invitation.getInvitationKey());

        // when
        ExtractableResponse<?> response = with()
                .body(request)
                .when()
                .request(POST, "/groups/{groupId}/invitations/{invitationId}/accept",
                        groupToInvite.getId() + 3, invitation.getId())
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_BAD_REQUEST);
        assertThat(response.jsonPath().getString("code")).isEqualTo(ErrorCode.INVALID_PAYLOAD.getCode());
        assertThat(response.jsonPath().getString("message"))
                .contains("Invalid groupId or invitationKey");
    }

    @Test
    @DisplayName("그룹에 챌린지를 등록한다")
    void addChallengeToGroup_Success() {
        // given
        Group groupToQuery = groupSupport.callToMakeGroup(List.of(currentUser));

        ChallengeCreationRequest request = new ChallengeCreationRequest(
                "test challenge", "test description",
                List.of(new TaskRequest("test task 1", ZonedDateTime.now().plusHours(3)),
                        new TaskRequest("test task 2", ZonedDateTime.now().plusHours(6)),
                        new TaskRequest("test task 3", ZonedDateTime.now().plusHours(9))),
                10L);

        // when
        ExtractableResponse<?> response = with()
                .body(request)
                .when().request(POST, "/groups/{groupId}/challenges", groupToQuery.getId())
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(response.jsonPath().getObject("data", ChallengeResponse.class))
                .satisfies(data -> {
                    assertThat(this.validator.validate(data)).isEmpty();
                    assertThat(data.title()).isEqualTo(request.title());
                    assertThat(data.deadline()).isEqualTo(request.tasks().get(2).deadline());
                    assertThat(data.description()).isEqualTo(request.description());
                });
    }

    @Test
    @DisplayName("그룹에 챌린지를 등록할 때 포인트가 부족하면 실패한다")
    void addChallengeToGroup_InsufficientPoints() {
        // given
        Group groupToQuery = groupSupport.callToMakeGroup(List.of(currentUser));

        ChallengeCreationRequest request = new ChallengeCreationRequest(
                "test challenge", "test description",
                List.of(new TaskRequest("test task 1", ZonedDateTime.now().plusHours(3)),
                        new TaskRequest("test task 2", ZonedDateTime.now().plusHours(6)),
                        new TaskRequest("test task 3", ZonedDateTime.now().plusHours(9))),
                10_000L);

        // when
        ExtractableResponse<?> response = with()
                .body(request)
                .when().request(POST, "/groups/{groupId}/challenges", groupToQuery.getId())
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_BAD_REQUEST);
        assertThat(response.jsonPath().getString("code")).isEqualTo(ErrorCode.INSUFFICIENT_POINTS.getCode());
        assertThat(response.jsonPath().getString("message")).contains("Insufficient equity to deduct");
    }

    @Test
    @DisplayName("그룹에 챌린지를 등록할 때 마감일이 현재 시각보다 이전인 테스크가 있으면 오류로 응답한다")
    void addChallengeToGroup_PastTaskDeadline() {
        // given
        Group groupToQuery = groupSupport.callToMakeGroup(List.of(currentUser));

        ChallengeCreationRequest request = new ChallengeCreationRequest(
                "test challenge", "test description",
                List.of(new TaskRequest("test task 1", ZonedDateTime.now().minusHours(3)),
                        new TaskRequest("test task 2", ZonedDateTime.now().plusHours(6)),
                        new TaskRequest("test task 3", ZonedDateTime.now().plusHours(9))),
                10L);

        // when
        ExtractableResponse<?> response = with()
                .body(request)
                .when().request(POST, "/groups/{groupId}/challenges", groupToQuery.getId())
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_BAD_REQUEST);
        assertThat(response.jsonPath().getString("code")).isEqualTo(ErrorCode.INVALID_PAYLOAD.getCode());
        assertThat(response.jsonPath().getString("message")).isEqualTo(ErrorCode.INVALID_PAYLOAD.getDefaultMessage());
    }

    @Test
    @DisplayName("그룹의 챌린지 목록을 조회한다")
    void getChallenges_Success() {
        // given
        User anotherUser = userSupport.registerUserToDB();
        Group groupToQuery = groupSupport.callToMakeGroup(List.of(currentUser, anotherUser));

        Challenge myInProgressChallenge = challengeSupport
                .callToMakeChallengesWithTasks(groupToQuery, 5, 3, currentUser);
        Challenge myCompletedChallenge = challengeSupport
                .callToMakeCompletedChallengeWithTasks(groupToQuery, 4, currentUser);
        Challenge anotherUserChallenge = challengeSupport
                .callToMakeCompletedChallengeWithTasks(groupToQuery, 5, anotherUser);

        // when
        groupSupport.callToLeaveGroup(anotherUser, groupToQuery.getId());
        ExtractableResponse<?> response = with()
                .when().request(GET, "/groups/{groupId}/challenges", groupToQuery.getId())
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(response.jsonPath().getObject("data", GroupChallengeListResponse.class))
                .satisfies(data -> {
                    assertThat(this.validator.validate(data)).isEmpty();
                    assertThat(data.challenges()).hasSize(3);
                    assertThat(data.challenges())
                            .map(GroupChallengeListItem::recentCertification)
                            .noneMatch(Objects::isNull);
                    assertThat(data.challenges())
                            .map(GroupChallengeListItem::isCompleted)
                            .containsExactly(false, true, true);
                    assertThat(data.challenges())
                            .map(GroupChallengeListItem::challengerDetail)
                            .map(ChallengerDTO::currentlyJoined)
                            .containsExactly(true, true, false);
                });
    }

    @Test
    @DisplayName("그룹의 챌린지 목록을 조회한다 - 마지막 인증 순서대로")
    void getChallenges_Success_OrderedByLastCertification() throws MalformedURLException {
        // given
        User anotherUser = userSupport.registerUserToDB();
        Group groupToQuery = groupSupport.callToMakeGroup(List.of(currentUser, anotherUser));

        Challenge myInProgressChallenge = challengeSupport
                .callToMakeChallengesWithTasks(groupToQuery, 5, 3, currentUser);
        Challenge myCompletedChallenge = challengeSupport
                .callToMakeCompletedChallengeWithTasks(groupToQuery, 4, currentUser);
        Challenge anotherUserChallenge = challengeSupport
                .callToMakeCompletedChallengeWithTasks(groupToQuery, 5, anotherUser);

        challengeSupport.callToCertifyTask(currentUser, myInProgressChallenge,
                myInProgressChallenge.getTasks().get(3).getId(),
                new TaskCertificationRequest(List.of(new URL("https://blog.com/article1")), List.of()));

        // when
        groupSupport.callToLeaveGroup(anotherUser, groupToQuery.getId());
        ExtractableResponse<?> response = with()
                .queryParam("sortBy", ChallengeSortType.LATEST_CERTIFICATION)
                .when().request(GET, "/groups/{groupId}/challenges", groupToQuery.getId())
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(response.jsonPath().getObject("data", GroupChallengeListResponse.class))
                .satisfies(data -> {
                    assertThat(this.validator.validate(data)).isEmpty();
                    assertThat(data.challenges()).hasSize(3);
                    assertThat(data.challenges())
                            .map(GroupChallengeListItem::recentCertification)
                            .noneMatch(Objects::isNull);
                    assertThat(data.challenges())
                            .map(GroupChallengeListItem::isCompleted)
                            .containsExactly(false, true, true);
                    assertThat(data.challenges())
                            .map(GroupChallengeListItem::challengerDetail)
                            .map(ChallengerDTO::currentlyJoined)
                            .containsExactly(true, false, true);
                });
    }

    @Test
    @DisplayName("그룹의 테스크 목록을 조회한다")
    void getTasks_Success() {
        // given
        User anotherUser = userSupport.registerUserToDB();
        Group groupToQuery = groupSupport.callToMakeGroup(List.of(currentUser, anotherUser));

        Challenge myInProgressChallenge = challengeSupport
                .callToMakeChallengesWithTasks(groupToQuery, 3, 1, currentUser);
        Challenge myCompletedChallenge = challengeSupport
                .callToMakeCompletedChallengeWithTasks(groupToQuery, 4, currentUser);
        Challenge anotherUserChallenge = challengeSupport
                .callToMakeCompletedChallengeWithTasks(groupToQuery, 2, anotherUser);

        // when
        groupSupport.callToLeaveGroup(anotherUser, groupToQuery.getId());
        ExtractableResponse<?> response = with()
                .when().request(GET, "/groups/{groupId}/tasks", groupToQuery.getId())
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(response.jsonPath().getObject("data", GroupTaskListResponse.class))
                .satisfies(data -> {
                    assertThat(this.validator.validate(data)).isEmpty();
                    assertThat(data.tasks()).hasSize(9);
                    assertThat(data.tasks()).map(GroupTaskListItem::certification)
                            .anyMatch(Objects::isNull);
                    assertThat(data.tasks()).map(GroupTaskListItem::certification)
                            .anyMatch(Objects::nonNull);
                    assertThat(data.tasks())
                            .map(GroupTaskListItem::challengerDetail)
                            .map(ChallengerDTO::currentlyJoined)
                            .containsExactly(true, true, true, true, true, true, true, false, false);
                    assertThat(data.tasks())
                            .map(GroupTaskListItem::challengeDetail)
                            .map(TaskChallengeDTO::isCompleted)
                            .containsExactly(false, false, false, true, true, true, true, true, true);
                });
    }

    @Test
    @DisplayName("그룹의 테스크 목록을 조회한다 - 진행중인 테스크만")
    void getTasks_InProgress_Success() {
        // given
        User anotherUser = userSupport.registerUserToDB();
        Group groupToQuery = groupSupport.callToMakeGroup(List.of(currentUser, anotherUser));

        Challenge myInProgressChallenge = challengeSupport
                .callToMakeChallengesWithTasks(groupToQuery, 3, 1, currentUser);
        Challenge myCompletedChallenge = challengeSupport
                .callToMakeCompletedChallengeWithTasks(groupToQuery, 4, currentUser);
        Challenge anotherUserChallenge = challengeSupport
                .callToMakeCompletedChallengeWithTasks(groupToQuery, 2, anotherUser);

        // when
        groupSupport.callToLeaveGroup(anotherUser, groupToQuery.getId());
        ExtractableResponse<?> response = with()
                .queryParam("status", TaskCertificationStatus.IN_PROGRESS)
                .when().request(GET, "/groups/{groupId}/tasks", groupToQuery.getId())
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(response.jsonPath().getObject("data", GroupTaskListResponse.class))
                .satisfies(data -> {
                    assertThat(this.validator.validate(data)).isEmpty();
                    assertThat(data.tasks()).hasSize(2);
                    assertThat(data.tasks()).map(GroupTaskListItem::certification)
                            .allMatch(Objects::isNull);
                    assertThat(data.tasks())
                            .map(GroupTaskListItem::challengerDetail)
                            .map(ChallengerDTO::currentlyJoined)
                            .containsExactly(true, true);
                    assertThat(data.tasks())
                            .map(GroupTaskListItem::challengeDetail)
                            .map(TaskChallengeDTO::isCompleted)
                            .containsExactly(false, false);
                });
    }

    @Test
    @DisplayName("그룹의 채팅 메시지를 페이지네이션하여 조회한다")
    void getMessages_Success() {
        // given
        Group groupToQuery = groupSupport.registerGroupToDB(List.of(currentUser));
        Long pageNumber = 0L;
        Long pageSize = 2L;
        List<ChatMessage> messages = groupSupport.registerChatMessagesToDB(groupToQuery, currentUser,
                List.of("첫 번째 메시지", "두 번째 메시지", "세 번째 메시지"),
                List.of(List.of(), List.of(), List.of(new HuntTaskChatAction(1L, 1L))));

        // when
        ExtractableResponse<?> response = with()
                .queryParam("pageNum", pageNumber)
                .queryParam("pageSize", pageSize)
                .queryParam("sortBy", "LATEST")
                .when().request(GET, "/groups/{groupId}/messages", groupToQuery.getId())
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(response.jsonPath().getObject("data", ChatMessageListResponse.class))
                .satisfies(data -> {
                    assertThat(this.validator.validate(data)).isEmpty();
                    assertThat(data.messages()).hasSize(2);
                    assertThat(data.currentPage()).isEqualTo(0);
                    assertThat(data.pageCount()).isEqualTo(2);
                    assertThat(data.chatMessageCount()).isEqualTo(3);
                    assertThat(data.messages().stream().map(ChatMessageResponse::chatActionList).toList())
                            .satisfies(actions -> {
                                assertThat(actions).isNotEmpty().hasSize(1);
                                assertThat(actions.get(0)).hasOnlyElementsOfType(ChatActionItem.class);
                            });
                    assertThat(data.messages().stream().map(ChatMessageResponse::content).toList())
                            .containsExactly("세 번째 메시지", "두 번째 메시지"); // 최신순 정렬 확인
                });
    }

    @Test
    @DisplayName("존재하지 않는 그룹의 채팅 메시지를 조회하면 오류로 응답한다")
    void getMessages_EmptyGroup() {
        // given
        Long groupIdThatDoesNotExist = 1L;
        assert groupRepository.findAll().isEmpty();
        assert chatMessageRepository.findAll().isEmpty();

        // when
        ExtractableResponse<?> response = with()
                .when().request(GET, "/groups/{groupId}/messages", groupIdThatDoesNotExist)
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_NOT_FOUND);
        assertThat(response.jsonPath().getString("code")).isEqualTo(ErrorCode.ENTITY_NOT_FOUND.getCode());
        assertThat(response.jsonPath().getString("message"))
                .contains("Group not found");
    }

    @Test
    @DisplayName("채팅 메시지가 없는 그룹을 조회하면 빈 목록을 반환한다")
    void getMessages_EmptyMessages() {
        // given
        Group groupToQuery = groupSupport.registerGroupToDB(List.of(currentUser));
        assert chatMessageRepository.findAll().isEmpty();

        // when
        ExtractableResponse<?> response = with()
                .when().request(GET, "/groups/{groupId}/messages", groupToQuery.getId())
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(response.jsonPath().getObject("data", ChatMessageListResponse.class))
                .satisfies(data -> {
                    assertThat(this.validator.validate(data)).isEmpty();
                    assertThat(data.messages()).isEmpty();
                    assertThat(data.currentPage()).isEqualTo(0);
                    assertThat(data.pageCount()).isEqualTo(0);
                    assertThat(data.chatMessageCount()).isEqualTo(0);
                });
    }

    @Test
    @DisplayName("그룹 멤버 목록 조회에 성공한다")
    void getGroupMembers_Success() {
        // given
        User anotherMember1 = userSupport.registerUserToDB();
        User anotherMember2 = userSupport.registerUserToDB();
        User anotherMember3 = userSupport.registerUserToDB();
        User anotherMember4 = userSupport.registerUserToDB();
        Group groupToQuery = groupSupport.callToMakeGroup(
                List.of(currentUser, anotherMember1, anotherMember2, anotherMember3, anotherMember4));

        // when
        ExtractableResponse<?> response = with()
                .when().request(GET, "/groups/{groupId}/members", groupToQuery.getId())
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(response.jsonPath().getObject("data", GroupMemberListResponse.class))
                .satisfies(data -> {
                    assertThat(this.validator.validate(data)).isEmpty();
                    assertThat(data.members()).hasSize(5);
                });
    }

    @Test
    @DisplayName("그룹 멤버 목록 조회 - 보유 포인트 기준 정렬")
    void getGroupMembers_Success_OrderedByPoints() {
        // given
        User has1500PointMember = userSupport.registerUserToDBWithPoints(1500L);
        User has2000PointMember = userSupport.registerUserToDBWithPoints(2000L);
        User has500PointMember = userSupport.registerUserToDBWithPoints(500L);
        User has800PointMember = userSupport.registerUserToDBWithPoints(800L);
        Group groupToQuery = groupSupport.callToMakeGroup(
                List.of(currentUser, has1500PointMember, has2000PointMember, has500PointMember, has800PointMember));

        // when
        ExtractableResponse<?> response = with()
                .queryParam("sortBy", GroupMemberSortType.POINT)
                .when().request(GET, "/groups/{groupId}/members", groupToQuery.getId())
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(response.jsonPath().getObject("data", GroupMemberListResponse.class))
                .satisfies(data -> {
                    assertThat(this.validator.validate(data)).isEmpty();
                    assertThat(data.members()).hasSize(5);
                    assertThat(data.members())
                            .map(GroupMemberListItem::userId)
                            .containsExactly(
                                    has2000PointMember.getId(),
                                    has1500PointMember.getId(),
                                    currentUser.getId(),
                                    has800PointMember.getId(),
                                    has500PointMember.getId());
                });
    }

    @Test
    @DisplayName("그룹 멤버 목록 조회 - 챌린지 평균 완수율 기준 정렬")
    void getGroupMembers_Success_OrderedByChallengeCompletionRate() {
        // given
        User lowRateUser = userSupport.registerUserToDB();
        User highRateUser = userSupport.registerUserToDB();
        Group groupToQuery = groupSupport.callToMakeGroup(List.of(currentUser, highRateUser, lowRateUser));

        Challenge lowRateSucceedChallenge = challengeSupport
                .callToMakeCompletedChallengeWithTasks(groupToQuery, 3, lowRateUser);
        Challenge lowRateFailedChallenge1 = challengeSupport
                .callToMakeChallengeWithFailedTasks(groupToQuery, 5, lowRateUser);
        Challenge lowRateFailedChallenge2 = challengeSupport
                .callToMakeChallengeWithFailedTasks(groupToQuery, 5, lowRateUser);

        Challenge highRateSucceedChallenge = challengeSupport
                .callToMakeCompletedChallengeWithTasks(groupToQuery, 5, highRateUser);
        Challenge highRateFailedChallenge = challengeSupport
                .callToMakeChallengeWithFailedTasks(groupToQuery, 1, highRateUser);

        // when
        ExtractableResponse<?> response = with()
                .queryParam("sortBy", GroupMemberSortType.AVERAGE_CHALLENGE_COMPLETION_RATE)
                .when().request(GET, "/groups/{groupId}/members", groupToQuery.getId())
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(response.jsonPath().getObject("data", GroupMemberListResponse.class))
                .satisfies(data -> {
                    assertThat(this.validator.validate(data)).isEmpty();
                    assertThat(data.members()).hasSize(3);
                    assertThat(data.members())
                            .map(GroupMemberListItem::userId)
                            .containsExactly(
                                    highRateUser.getId(),
                                    lowRateUser.getId(),
                                    currentUser.getId());
                });
    }

    @Test
    @DisplayName("그룹 멤버 목록 조회 - 헌팅 횟수 기준 정렬")
    void getGroupMembers_Success_OrderedByHuntingCount() {
        // given
        User lowHuntingUser = userSupport.registerUserToDB();
        User highHuntingUser = userSupport.registerUserToDB();
        Group groupToQuery = groupSupport.callToMakeGroup(List.of(currentUser, highHuntingUser, lowHuntingUser));

        Challenge failedChallengeForHunting = challengeSupport
                .callToMakeChallengeWithFailedTasks(groupToQuery, 5, currentUser);

        challengeSupport.callToHuntTask(lowHuntingUser, failedChallengeForHunting,
                failedChallengeForHunting.getTasks().get(0));

        challengeSupport.callToHuntTask(highHuntingUser, failedChallengeForHunting,
                failedChallengeForHunting.getTasks().get(1));
        challengeSupport.callToHuntTask(highHuntingUser, failedChallengeForHunting,
                failedChallengeForHunting.getTasks().get(2));

        // when
        ExtractableResponse<?> response = with()
                .queryParam("sortBy", GroupMemberSortType.HUNTING_COUNT)
                .when().request(GET, "/groups/{groupId}/members", groupToQuery.getId())
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(response.jsonPath().getObject("data", GroupMemberListResponse.class))
                .satisfies(data -> {
                    assertThat(this.validator.validate(data)).isEmpty();
                    assertThat(data.members()).hasSize(3);
                    assertThat(data.members())
                            .map(GroupMemberListItem::userId)
                            .containsExactly(
                                    highHuntingUser.getId(),
                                    lowHuntingUser.getId(),
                                    currentUser.getId());
                });
    }
}
