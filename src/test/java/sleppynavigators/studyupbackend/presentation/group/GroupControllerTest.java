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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import sleppynavigators.studyupbackend.common.RestAssuredBaseTest;
import sleppynavigators.studyupbackend.domain.authentication.token.AccessToken;
import sleppynavigators.studyupbackend.domain.authentication.token.AccessTokenProperties;
import sleppynavigators.studyupbackend.domain.challenge.Challenge;
import sleppynavigators.studyupbackend.domain.chat.ChatMessage;
import sleppynavigators.studyupbackend.domain.chat.SenderType;
import sleppynavigators.studyupbackend.domain.group.Group;
import sleppynavigators.studyupbackend.domain.group.invitation.GroupInvitation;
import sleppynavigators.studyupbackend.domain.user.User;
import sleppynavigators.studyupbackend.domain.user.vo.UserProfile;
import sleppynavigators.studyupbackend.exception.ErrorCode;
import sleppynavigators.studyupbackend.infrastructure.challenge.ChallengeRepository;
import sleppynavigators.studyupbackend.infrastructure.chat.ChatMessageRepository;
import sleppynavigators.studyupbackend.infrastructure.group.GroupRepository;
import sleppynavigators.studyupbackend.infrastructure.group.invitation.GroupInvitationRepository;
import sleppynavigators.studyupbackend.infrastructure.user.UserRepository;
import sleppynavigators.studyupbackend.presentation.challenge.dto.request.ChallengeCreationRequest;
import sleppynavigators.studyupbackend.presentation.challenge.dto.request.ChallengeCreationRequest.TaskRequest;
import sleppynavigators.studyupbackend.presentation.challenge.dto.response.ChallengeResponse;
import sleppynavigators.studyupbackend.presentation.chat.dto.response.ChatMessageDto;
import sleppynavigators.studyupbackend.presentation.chat.dto.response.ChatMessageListResponse;
import sleppynavigators.studyupbackend.presentation.group.dto.request.GroupCreationRequest;
import sleppynavigators.studyupbackend.presentation.group.dto.request.GroupInvitationAcceptRequest;
import sleppynavigators.studyupbackend.presentation.group.dto.response.GroupChallengeListResponse;
import sleppynavigators.studyupbackend.presentation.group.dto.response.GroupChallengeListResponse.GroupChallengeListItem;
import sleppynavigators.studyupbackend.presentation.group.dto.response.GroupInvitationResponse;
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
    private UserRepository userRepository;

    @Autowired
    private ChallengeRepository challengeRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private AccessTokenProperties accessTokenProperties;

    private User currentUser;

    @BeforeEach
    void setUp() {
        currentUser = TestFixtureMother.registerUser(userRepository);
        String bearerToken = TestFixtureMother.createBearerToken(currentUser, accessTokenProperties);
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .addHeader("Authorization", bearerToken)
                .build();
    }

    @Test
    @DisplayName("사용자가 그룹 생성에 성공한다")
    void memberGroupCreation_Success() {
        // given
        GroupCreationRequest request =
                new GroupCreationRequest("test group", "test description", "https://test.com");

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
    @DisplayName("그룹 생성 시 이름이 없으면 오류로 응답한다")
    void groupCreation_BlankName() {
        // given
        GroupCreationRequest request =
                new GroupCreationRequest("", "test description", "https://test.com");

        // when
        ExtractableResponse<?> response = with()
                .body(request)
                .when().request(POST, "/groups")
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_BAD_REQUEST);
        assertThat(response.jsonPath().getString("code")).isEqualTo(ErrorCode.INVALID_API.getCode());
        assertThat(response.jsonPath().getString("message")).isEqualTo(ErrorCode.INVALID_API.getDefaultMessage());
    }

    @Test
    @DisplayName("그룹 생성 시 설명이 없으면 오류로 응답한다")
    void groupCreation_BlankDescription() {
        // given
        GroupCreationRequest request =
                new GroupCreationRequest("test group", "", "https://test.com");

        // when
        ExtractableResponse<?> response = with()
                .body(request)
                .when().request(POST, "/groups")
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_BAD_REQUEST);
        assertThat(response.jsonPath().getString("code")).isEqualTo(ErrorCode.INVALID_API.getCode());
        assertThat(response.jsonPath().getString("message")).isEqualTo(ErrorCode.INVALID_API.getDefaultMessage());
    }

    @Test
    @DisplayName("그룹 생성 시 이름이 너무 길면 오류로 응답한다")
    void groupCreation_TooLongName() {
        // given
        GroupCreationRequest request =
                new GroupCreationRequest("a".repeat(101), "test description", "https://test.com");

        // when
        ExtractableResponse<?> response = with()
                .body(request)
                .when().request(POST, "/groups")
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_BAD_REQUEST);
        assertThat(response.jsonPath().getString("code")).isEqualTo(ErrorCode.INVALID_PAYLOAD.getCode());
        assertThat(response.jsonPath().getString("message")).isEqualTo(ErrorCode.INVALID_PAYLOAD.getDefaultMessage());
    }

    @Test
    @DisplayName("그룹 생성 시 설명이 너무 길면 오류로 응답한다")
    void groupCreation_TooLongDescription() {
        // given
        GroupCreationRequest request =
                new GroupCreationRequest("test group", "a".repeat(1001), "https://test.com");

        // when
        ExtractableResponse<?> response = with()
                .body(request)
                .when().request(POST, "/groups")
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_BAD_REQUEST);
        assertThat(response.jsonPath().getString("code")).isEqualTo(ErrorCode.INVALID_PAYLOAD.getCode());
        assertThat(response.jsonPath().getString("message")).isEqualTo(ErrorCode.INVALID_PAYLOAD.getDefaultMessage());
    }

    @Test
    @DisplayName("사용자가 그룹에서 탈퇴에 성공한다")
    void memberLeaveGroup_Success() {
        // given
        User anotherMember = TestFixtureMother.registerUser(userRepository);
        Group groupToLeave = TestFixtureMother.registerGroup(List.of(currentUser, anotherMember), groupRepository);

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
        Group groupToLeave = TestFixtureMother.registerGroup(List.of(currentUser), groupRepository);

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
        Group groupToLeave = TestFixtureMother.registerGroupWithChallengesAndTasks(List.of(
                new int[]{3, 1},
                new int[]{2, 0},
                new int[]{5, 2}
        ), currentUser, groupRepository, challengeRepository);

        // when
        ExtractableResponse<?> response = with()
                .when().request(POST, "/groups/{groupId}/leave", groupToLeave.getId())
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_BAD_REQUEST);
        assertThat(response.jsonPath().getString("code")).isEqualTo(ErrorCode.ACTION_REQUIRED_BEFORE.getCode());
        assertThat(response.jsonPath().getString("message")).isEqualTo("Challenger cannot leave the group.");
    }


    @Test
    @DisplayName("그룹 초대를 생성한다")
    void createGroupInvitation_Success() {
        // given
        Group groupToInvite = TestFixtureMother.registerGroup(List.of(currentUser), groupRepository);

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
        assertThat(response.jsonPath().getString("message")).isEqualTo(ErrorCode.ENTITY_NOT_FOUND.getDefaultMessage());
    }

    @Test
    @DisplayName("그룹 초대를 조회한다")
    void getGroupInvitations_Success() {
        // given
        Group groupToInvite = TestFixtureMother.registerGroup(List.of(currentUser), groupRepository);
        GroupInvitation invitation = TestFixtureMother
                .registerGroupInvitation(groupToInvite, groupInvitationRepository);

        // when
        ExtractableResponse<?> response = with()
                .when()
                .request(GET, "/groups/{groupId}/invitations/{invitationId}", groupToInvite.getId(), invitation.getId())
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(response.jsonPath().getObject("data", GroupResponse.class))
                .satisfies(data -> assertThat(this.validator.validate(data)).isEmpty());
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
        assertThat(response.jsonPath().getString("message")).isEqualTo(ErrorCode.ENTITY_NOT_FOUND.getDefaultMessage());
    }

    @Test
    @DisplayName("그룹 초대를 수락한다")
    void acceptGroupInvitation_Success() {
        // given
        Group groupToInvite = TestFixtureMother.registerGroup(List.of(currentUser), groupRepository);
        GroupInvitation invitation = TestFixtureMother
                .registerGroupInvitation(groupToInvite, groupInvitationRepository);

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
        assertThat(response.jsonPath().getObject("data", GroupResponse.class))
                .satisfies(data -> assertThat(this.validator.validate(data)).isEmpty());
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
        assertThat(response.jsonPath().getString("message")).isEqualTo(ErrorCode.ENTITY_NOT_FOUND.getDefaultMessage());
    }

    @Test
    @DisplayName("그룹 초대 수락 시 초대 키가 일치하지 않으면 오류로 응답한다")
    void acceptGroupInvitation_InvalidKey() {
        // given
        Group groupToInvite = TestFixtureMother.registerGroup(List.of(currentUser), groupRepository);
        GroupInvitation invitation = TestFixtureMother
                .registerGroupInvitation(groupToInvite, groupInvitationRepository);

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
        assertThat(response.jsonPath().getString("message")).isEqualTo(ErrorCode.INVALID_PAYLOAD.getDefaultMessage());
    }

    @Test
    @DisplayName("그룹 초대 수락 시 주어진 그룹과 일치하지 않으면 오류로 응답한다")
    void acceptGroupInvitation_InvalidGroup() {
        // given
        Group groupToInvite = TestFixtureMother.registerGroup(List.of(currentUser), groupRepository);
        GroupInvitation invitation = TestFixtureMother
                .registerGroupInvitation(groupToInvite, groupInvitationRepository);

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
        assertThat(response.jsonPath().getString("message")).isEqualTo(ErrorCode.INVALID_PAYLOAD.getDefaultMessage());
    }

    @Test
    @DisplayName("그룹에 챌린지를 등록한다")
    void addChallengeToGroup_Success() {
        // given
        Group groupToQuery = TestFixtureMother.registerGroup(List.of(currentUser), groupRepository);

        assert challengeRepository.findAllByGroupId(groupToQuery.getId()).isEmpty();

        ChallengeCreationRequest request = new ChallengeCreationRequest("test challenge",
                LocalDateTime.now().plusDays(3), "test description", List.of(
                new TaskRequest("test task 1", LocalDateTime.now().plusHours(3)),
                new TaskRequest("test task 2", LocalDateTime.now().plusHours(6)),
                new TaskRequest("test task 3", LocalDateTime.now().plusHours(9))
        ));

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
                    assertThat(data.deadline()).isEqualTo(request.deadline());
                    assertThat(data.description()).isEqualTo(request.description());
                });
    }

    @Test
    @DisplayName("존재하지 않는 그룹에 챌린지를 등록하면 오류로 응답한다")
    void addChallengeToGroup_NotFound() {
        // given
        Long groupIdThatDoesNotExist = 1L;
        assert groupRepository.findAll().isEmpty();

        ChallengeCreationRequest request = new ChallengeCreationRequest("test challenge",
                LocalDateTime.now().plusDays(3), "test description", List.of(
                new TaskRequest("test task 1", LocalDateTime.now().plusHours(3)),
                new TaskRequest("test task 2", LocalDateTime.now().plusHours(6)),
                new TaskRequest("test task 3", LocalDateTime.now().plusHours(9))
        ));

        // when
        ExtractableResponse<?> response = with()
                .body(request)
                .when().request(POST, "/groups/{groupId}/challenges", groupIdThatDoesNotExist)
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_NOT_FOUND);
        assertThat(response.jsonPath().getString("code")).isEqualTo(ErrorCode.ENTITY_NOT_FOUND.getCode());
        assertThat(response.jsonPath().getString("message")).isEqualTo(ErrorCode.ENTITY_NOT_FOUND.getDefaultMessage());
    }

    @Test
    @DisplayName("그룹에 챌린지를 등록할 때 제목이 없으면 오류로 응답한다")
    void addChallengeToGroup_BlankTitle() {
        // given
        Group groupToQuery = TestFixtureMother.registerGroup(List.of(currentUser), groupRepository);

        ChallengeCreationRequest request = new ChallengeCreationRequest("",
                LocalDateTime.now().plusDays(3), "test description", List.of(
                new TaskRequest("test task 1", LocalDateTime.now().plusHours(3)),
                new TaskRequest("test task 2", LocalDateTime.now().plusHours(6)),
                new TaskRequest("test task 3", LocalDateTime.now().plusHours(9))
        ));

        // when
        ExtractableResponse<?> response = with()
                .body(request)
                .when().request(POST, "/groups/{groupId}/challenges", groupToQuery.getId())
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_BAD_REQUEST);
        assertThat(response.jsonPath().getString("code")).isEqualTo(ErrorCode.INVALID_API.getCode());
        assertThat(response.jsonPath().getString("message")).isEqualTo(ErrorCode.INVALID_API.getDefaultMessage());
    }

    @Test
    @DisplayName("그룹에 챌린지를 등록할 때 이름이 너무 길면 오류로 응답한다")
    void addChallengeToGroup_TooLongTitle() {
        // given
        Group groupToQuery = TestFixtureMother.registerGroup(List.of(currentUser), groupRepository);

        ChallengeCreationRequest request = new ChallengeCreationRequest("a".repeat(101),
                LocalDateTime.now().plusDays(3), "test description", List.of(
                new TaskRequest("test task 1", LocalDateTime.now().plusHours(3)),
                new TaskRequest("test task 2", LocalDateTime.now().plusHours(6)),
                new TaskRequest("test task 3", LocalDateTime.now().plusHours(9))
        ));

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
    @DisplayName("그룹에 챌린지를 등록할 때 설명이 너무 길면 오류로 응답한다")
    void addChallengeToGroup_TooLongDescription() {
        // given
        Group groupToQuery = TestFixtureMother.registerGroup(List.of(currentUser), groupRepository);

        ChallengeCreationRequest request = new ChallengeCreationRequest("test challenge",
                LocalDateTime.now().plusDays(3), "a".repeat(1001), List.of(
                new TaskRequest("test task 1", LocalDateTime.now().plusHours(3)),
                new TaskRequest("test task 2", LocalDateTime.now().plusHours(6)),
                new TaskRequest("test task 3", LocalDateTime.now().plusHours(9))
        ));

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
    @DisplayName("그룹에 챌린지를 등록할 때 마감일이 현재 시각보다 이전이면 오류로 응답한다")
    void addChallengeToGroup_PastDeadline() {
        // given
        Group groupToQuery = TestFixtureMother.registerGroup(List.of(currentUser), groupRepository);

        ChallengeCreationRequest request = new ChallengeCreationRequest("test challenge",
                LocalDateTime.now().minusDays(1), "test description", List.of(
                new TaskRequest("test task 1", LocalDateTime.now().plusHours(3)),
                new TaskRequest("test task 2", LocalDateTime.now().plusHours(6)),
                new TaskRequest("test task 3", LocalDateTime.now().plusHours(9))
        ));

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
    @DisplayName("그룹에 챌린지를 등록할 때 이름이 없는 테스크가 있으면 오류로 응답한다")
    void addChallengeToGroup_BlankTaskName() {
        // given
        Group groupToQuery = TestFixtureMother.registerGroup(List.of(currentUser), groupRepository);

        ChallengeCreationRequest request = new ChallengeCreationRequest("test challenge",
                LocalDateTime.now().plusDays(3), "test description", List.of(
                new TaskRequest("", LocalDateTime.now().plusHours(3)),
                new TaskRequest("test task 2", LocalDateTime.now().plusHours(6)),
                new TaskRequest("test task 3", LocalDateTime.now().plusHours(9))
        ));

        // when
        ExtractableResponse<?> response = with()
                .body(request)
                .when().request(POST, "/groups/{groupId}/challenges", groupToQuery.getId())
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_BAD_REQUEST);
        assertThat(response.jsonPath().getString("code")).isEqualTo(ErrorCode.INVALID_API.getCode());
        assertThat(response.jsonPath().getString("message")).isEqualTo(ErrorCode.INVALID_API.getDefaultMessage());
    }

    @Test
    @DisplayName("그룹에 챌린지를 등록할 때 이름이 너무 긴 테스크가 있으면 오류로 응답한다")
    void addChallengeToGroup_TooLongTaskName() {
        // given
        Group groupToQuery = TestFixtureMother.registerGroup(List.of(currentUser), groupRepository);

        ChallengeCreationRequest request = new ChallengeCreationRequest("test challenge",
                LocalDateTime.now().plusDays(3), "test description", List.of(
                new TaskRequest("a".repeat(101), LocalDateTime.now().plusHours(3)),
                new TaskRequest("test task 2", LocalDateTime.now().plusHours(6)),
                new TaskRequest("test task 3", LocalDateTime.now().plusHours(9))
        ));

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
    @DisplayName("그룹에 챌린지를 등록할 때 마감일이 없는 테스크가 있으면 오류로 응답한다")
    void addChallengeToGroup_NullTaskDeadline() {
        // given
        Group groupToQuery = TestFixtureMother.registerGroup(List.of(currentUser), groupRepository);

        ChallengeCreationRequest request = new ChallengeCreationRequest("test challenge",
                LocalDateTime.now().plusDays(3), "test description", List.of(
                new TaskRequest("test task 1", LocalDateTime.now().plusHours(3)),
                new TaskRequest("test task 2", null),
                new TaskRequest("test task 3", LocalDateTime.now().plusHours(9))
        ));

        // when
        ExtractableResponse<?> response = with()
                .body(request)
                .when().request(POST, "/groups/{groupId}/challenges", groupToQuery.getId())
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_BAD_REQUEST);
        assertThat(response.jsonPath().getString("code")).isEqualTo(ErrorCode.INVALID_API.getCode());
        assertThat(response.jsonPath().getString("message")).isEqualTo(ErrorCode.INVALID_API.getDefaultMessage());
    }

    @Test
    @DisplayName("그룹에 챌린지를 등록할 때 마감일이 현재 시각보다 이전인 테스크가 있으면 오류로 응답한다")
    void addChallengeToGroup_PastTaskDeadline() {
        // given
        Group groupToQuery = TestFixtureMother.registerGroup(List.of(currentUser), groupRepository);

        ChallengeCreationRequest request = new ChallengeCreationRequest("test challenge",
                LocalDateTime.now().plusDays(3), "test description", List.of(
                new TaskRequest("test task 1", LocalDateTime.now().minusHours(3)),
                new TaskRequest("test task 2", LocalDateTime.now().plusHours(6)),
                new TaskRequest("test task 3", LocalDateTime.now().plusHours(9))
        ));

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
        Group groupToQuery = TestFixtureMother.registerGroupWithChallengesAndTasks(List.of(
                new int[]{3, 1},
                new int[]{2, 0},
                new int[]{5, 2}
        ), currentUser, groupRepository, challengeRepository);

        // when
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
                    assertThat(data.challenges()).map(GroupChallengeListItem::recentCertification)
                            .anyMatch(Objects::isNull);
                    assertThat(data.challenges()).map(GroupChallengeListItem::recentCertification)
                            .anyMatch(Objects::nonNull);
                });
    }

    @Test
    @DisplayName("그룹의 테스크 목록을 조회한다")
    void getTasks_Success() {
        // given
        Group groupToQuery = TestFixtureMother.registerGroupWithChallengesAndTasks(List.of(
                new int[]{3, 1},
                new int[]{2, 0},
                new int[]{5, 2}
        ), currentUser, groupRepository, challengeRepository);

        // when
        ExtractableResponse<?> response = with()
                .when().request(GET, "/groups/{groupId}/tasks", groupToQuery.getId())
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(response.jsonPath().getObject("data", GroupTaskListResponse.class))
                .satisfies(data -> {
                    assertThat(this.validator.validate(data)).isEmpty();
                    assertThat(data.tasks()).hasSize(10);
                    assertThat(data.tasks()).map(GroupTaskListItem::certification)
                            .anyMatch(Objects::isNull);
                    assertThat(data.tasks()).map(GroupTaskListItem::certification)
                            .anyMatch(Objects::nonNull);
                });
    }

    @Test
    @DisplayName("그룹의 채팅 메시지를 페이지네이션하여 조회한다")
    void getMessages_Success() {
        // given
        Group groupToQuery = TestFixtureMother.registerGroup(List.of(currentUser), groupRepository);
        Long pageNumber = 0L;
        Long pageSize = 2L;
        List<ChatMessage> messages = TestFixtureMother.registerChatMessages(groupToQuery, currentUser,
                List.of("첫 번째 메시지",
                        "두 번째 메시지",
                        "세 번째 메시지"),
                List.of(LocalDateTime.now().plusSeconds(1),
                        LocalDateTime.now().plusSeconds(2),
                        LocalDateTime.now().plusSeconds(3)),
                chatMessageRepository);

        // when
        ExtractableResponse<?> response = with()
                .queryParam("page", pageNumber)
                .queryParam("size", pageSize)
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
                    assertThat(data.totalPages()).isEqualTo(2);
                    assertThat(data.totalElements()).isEqualTo(3);
                    assertThat(data.messages().stream().map(ChatMessageDto::content).toList())
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
        assertThat(response.jsonPath().getString("message")).isEqualTo(ErrorCode.ENTITY_NOT_FOUND.getDefaultMessage());
    }

    @Test
    @DisplayName("채팅 메시지가 없는 그룹을 조회하면 빈 목록을 반환한다")
    void getMessages_EmptyMessages() {
        // given
        Group groupToQuery = TestFixtureMother.registerGroup(List.of(currentUser), groupRepository);
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
                    assertThat(data.totalPages()).isEqualTo(0);
                    assertThat(data.totalElements()).isEqualTo(0);
                });
    }

    @Transactional
    private static class TestFixtureMother {

        /**
         * Generate a test user and save it to the database. `username` and `email` are set to "test-user" and
         * "test-email" respectively.
         *
         * @param userRepository UserRepository to save the user
         * @return The saved user
         */
        static User registerUser(UserRepository userRepository) {
            User user = new User("test-user", "test-email");
            return userRepository.save(user);
        }

        /**
         * Generate access token for the given user. The token can be used to authenticate the user for testing.
         *
         * @param user                  The user for testing
         * @param accessTokenProperties The properties for the access token
         * @return The generated access token as a string
         */
        static String createBearerToken(User user, AccessTokenProperties accessTokenProperties) {
            Long userId = user.getId();
            UserProfile userProfile = user.getUserProfile();
            List<String> authorities = List.of("profile");

            String accessToken = new AccessToken(userId, userProfile, authorities, accessTokenProperties)
                    .serialize(accessTokenProperties);
            return "Bearer " + accessToken;
        }

        /**
         * Generate a test group and save it to the database. `name` and `description` are set to "test-group" and
         * "test-group-description" respectively. The group is consists of the given members.
         *
         * @param members         The members of the group
         * @param groupRepository GroupRepository to save the group
         * @return The saved group
         */
        static Group registerGroup(List<User> members, GroupRepository groupRepository) {
            Group group = Group.builder()
                    .name("test-group")
                    .description("test-group-description")
                    .creator(members.get(0))
                    .build();

            for (User member : members) {
                group.addMember(member);
            }
            return groupRepository.save(group);
        }

        /**
         * Generate a test group invitation and save it to the database. The group invitation is created for the given
         * group.
         *
         * @param group                     The group for which the invitation is created
         * @param groupInvitationRepository GroupInvitationRepository to save the group invitation
         * @return The saved group invitation
         */
        static GroupInvitation registerGroupInvitation(
                Group group, GroupInvitationRepository groupInvitationRepository) {
            GroupInvitation groupInvitation = new GroupInvitation(group);
            return groupInvitationRepository.save(groupInvitation);
        }

        /**
         * Generate a test group with challenges and tasks, and save it to the database. The challenges are created
         * based on the given list of task progress.
         *
         * @param challengeListOrganizedByTaskProgress A list of challenges organized by task progress. Each element is
         *                                             an int array of size 2, where the first element is the number of
         *                                             tasks and the second element is the number of certified tasks.
         * @param creator                              The user who created the groups
         * @param groupRepository                      GroupRepository to save the groups
         * @param challengeRepository                  ChallengeRepository to save the challenges
         * @return The saved group
         */
        static Group registerGroupWithChallengesAndTasks(
                List<int[]> challengeListOrganizedByTaskProgress, User creator,
                GroupRepository groupRepository, ChallengeRepository challengeRepository) {

            Group group = Group.builder()
                    .name("test-group")
                    .description("test-group-description")
                    .creator(creator)
                    .build();
            groupRepository.save(group);

            for (int ci = 0; ci < challengeListOrganizedByTaskProgress.size(); ci++) {
                int[] taskProgress = challengeListOrganizedByTaskProgress.get(ci);
                Challenge challenge = Challenge.builder()
                        .title("test-challenge-" + ci)
                        .deadline(LocalDateTime.now().plusDays(3))
                        .group(group)
                        .owner(creator)
                        .build();

                int numOfTasks = taskProgress[0];
                int numOfCertified = taskProgress[1];
                for (int ti = 0; ti < numOfTasks; ti++) {
                    challenge.addTask("test-task-" + ci + "-" + ti, LocalDateTime.now().plusHours(3));

                    if (ti < numOfCertified) {
                        try {
                            challenge.getTasks().get(ti)
                                    .certify(List.of(), List.of(new URL("https://test.com")), creator);
                        } catch (MalformedURLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                challengeRepository.save(challenge);
            }
            return group;
        }

        /**
         * Generate a list of chat messages and save them to the database. The messages are created based on the given
         * list of contents and created times.
         *
         * @param group                 The group to which the messages belong
         * @param sender                The sender of the messages
         * @param contents              The contents of the messages
         * @param createdTimes          The created times of the messages
         * @param chatMessageRepository ChatMessageRepository to save the messages
         * @return The list of saved chat messages
         */
        static List<ChatMessage> registerChatMessages(
                Group group, User sender, List<String> contents, List<LocalDateTime> createdTimes,
                ChatMessageRepository chatMessageRepository) {

            List<ChatMessage> messages = new ArrayList<>();
            for (int i = 0; i < contents.size(); i++) {
                ChatMessage message = ChatMessage.builder()
                        .senderId(sender.getId())
                        .groupId(group.getId())
                        .content(contents.get(i))
                        .senderType(SenderType.USER)
                        .createdAt(createdTimes.get(i))
                        .build();
                messages.add(message);
            }
            return chatMessageRepository.saveAll(messages);
        }
    }
}
