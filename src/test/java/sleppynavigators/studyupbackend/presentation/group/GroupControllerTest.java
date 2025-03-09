package sleppynavigators.studyupbackend.presentation.group;

import static io.restassured.RestAssured.with;
import static io.restassured.http.Method.GET;
import static io.restassured.http.Method.POST;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import sleppynavigators.studyupbackend.domain.authentication.token.AccessToken;
import sleppynavigators.studyupbackend.domain.authentication.token.AccessTokenProperties;
import sleppynavigators.studyupbackend.domain.challenge.Challenge;
import sleppynavigators.studyupbackend.domain.chat.ChatMessage;
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
import sleppynavigators.studyupbackend.presentation.common.DatabaseCleaner;
import sleppynavigators.studyupbackend.presentation.group.dto.request.GroupCreationRequest;
import sleppynavigators.studyupbackend.presentation.group.dto.request.GroupInvitationAcceptRequest;
import sleppynavigators.studyupbackend.presentation.group.dto.response.GroupTaskListResponse.GroupTaskListItem;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("GroupController API 테스트")
public class GroupControllerTest {

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

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @LocalServerPort
    private int port;

    private User currentUser;

    @BeforeEach
    void setUp() {
        UserProfile userProfile = new UserProfile("guest", "example@guest.com");
        currentUser = userRepository.save(new User("guest", "example@guest.com"));

        AccessToken accessToken =
                new AccessToken(currentUser.getId(), userProfile, List.of("profile"), accessTokenProperties);
        String bearerToken = "Bearer " + accessToken.serialize(accessTokenProperties);

        RestAssured.port = port;
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .addHeader("Authorization", bearerToken)
                .build();
    }

    @AfterEach
    void tearDown() {
        databaseCleaner.execute();
        RestAssured.reset();
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
        assertThat(response.jsonPath().getString("data.id")).isNotBlank();
        assertThat(response.jsonPath().getString("data.name")).isNotBlank();
        assertThat(response.jsonPath().getString("data.description")).isNotBlank();
        assertThat(response.jsonPath().getString("data.thumbnailUrl")).isNotBlank();
        assertThat(groupRepository.findAll()).hasSize(1);
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
        User anotherMember = userRepository.save(new User("another", "example2@guest.com"));

        Group group = new Group("test group", "test description", "https://test.com", currentUser);
        group.addMember(anotherMember);
        Group savedGroup = groupRepository.save(group);

        // when
        ExtractableResponse<?> response = with()
                .when().request(POST, "/groups/{groupId}/leave", savedGroup.getId())
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(Optional.ofNullable(response.jsonPath().get("data"))).isEmpty();
        assertThat(groupRepository.findAll()).isNotEmpty();
    }

    @Test
    @DisplayName("유일한 사용자가 그룹에서 탈퇴한다 (그룹이 삭제된다)")
    void uniqueMemberLeaveGroup_Success() {
        // given
        Group group = new Group("test group", "test description", "https://test.com", currentUser);
        Group savedGroup = groupRepository.save(group);

        // when
        ExtractableResponse<?> response = with()
                .when().request(POST, "/groups/{groupId}/leave", savedGroup.getId())
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(Optional.ofNullable(response.jsonPath().get("data"))).isEmpty();
        assertThat(groupRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 그룹에 탈퇴를 요청하면 오류로 응답한다")
    void leaveGroup_NotFound() {
        // given
        assert groupRepository.findAll().isEmpty();

        // when
        ExtractableResponse<?> response = with()
                .when().request(POST, "/groups/{groupId}/leave", 1L)
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_NOT_FOUND);
        assertThat(response.jsonPath().getString("code")).isEqualTo(ErrorCode.ENTITY_NOT_FOUND.getCode());
        assertThat(response.jsonPath().getString("message")).isEqualTo(ErrorCode.ENTITY_NOT_FOUND.getDefaultMessage());
    }

    @Test
    @DisplayName("그룹 초대를 생성한다")
    void createGroupInvitation_Success() {
        // given
        Group group = groupRepository.save(
                new Group("test group", "test description", "https://test.com", currentUser));

        // when
        ExtractableResponse<?> response = with()
                .when().request(POST, "/groups/{groupId}/invitations", group.getId())
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(response.jsonPath().getString("data.invitationKey")).isNotBlank();
    }

    @Test
    @DisplayName("존재하지 않는 그룹에 초대를 생성하면 오류로 응답한다")
    void createGroupInvitation_NotFound() {
        // given
        assert groupRepository.findAll().isEmpty();

        // when
        ExtractableResponse<?> response = with()
                .when().request(POST, "/groups/{groupId}/invitations", 1L)
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
        Group group = groupRepository.save(
                new Group("test group", "test description", "https://test.com", currentUser));
        GroupInvitation invitation = groupInvitationRepository.save(new GroupInvitation(group));

        // when
        ExtractableResponse<?> response = with()
                .when().request(GET, "/groups/{groupId}/invitations/{invitationId}", group.getId(), invitation.getId())
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(response.jsonPath().getString("data.id")).isNotBlank();
        assertThat(response.jsonPath().getString("data.name")).isNotBlank();
        assertThat(response.jsonPath().getString("data.description")).isNotBlank();
        assertThat(response.jsonPath().getString("data.thumbnailUrl")).isNotBlank();
    }

    @Test
    @DisplayName("존재하지 않는 그룹 초대를 조회하면 오류로 응답한다")
    void getGroupInvitations_NotFound() {
        // given
        assert groupInvitationRepository.findAll().isEmpty();

        // when
        ExtractableResponse<?> response = with()
                .when().request(GET, "/groups/{groupId}/invitations/{invitationId}", 1L, 1L)
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
        Group group = groupRepository.save(
                new Group("test group", "test description", "https://test.com", currentUser));
        GroupInvitation invitation = groupInvitationRepository.save(new GroupInvitation(group));
        GroupInvitationAcceptRequest request = new GroupInvitationAcceptRequest(invitation.getInvitationKey());

        // when
        ExtractableResponse<?> response = with()
                .body(request)
                .when()
                .request(POST, "/groups/{groupId}/invitations/{invitationId}/accept", group.getId(), invitation.getId())
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(response.jsonPath().getString("data.id")).isNotBlank();
        assertThat(response.jsonPath().getString("data.name")).isNotBlank();
        assertThat(response.jsonPath().getString("data.description")).isNotBlank();
        assertThat(response.jsonPath().getString("data.thumbnailUrl")).isNotBlank();
    }

    @Test
    @DisplayName("존재하지 않는 그룹 초대를 수락하면 오류로 응답한다")
    void acceptGroupInvitation_NotFound() {
        // given
        assert groupRepository.findAll().isEmpty();
        GroupInvitationAcceptRequest request = new GroupInvitationAcceptRequest("invalid_key");

        // when
        ExtractableResponse<?> response = with()
                .body(request)
                .when().request(POST, "/groups/{groupId}/invitations/{invitationId}/accept", 1L, 1L)
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
        Group group = groupRepository.save(
                new Group("test group", "test description", "https://test.com", currentUser));
        GroupInvitation invitation = groupInvitationRepository.save(new GroupInvitation(group));
        GroupInvitationAcceptRequest request = new GroupInvitationAcceptRequest("invalid_key");

        // when
        ExtractableResponse<?> response = with()
                .body(request)
                .when()
                .request(POST, "/groups/{groupId}/invitations/{invitationId}/accept", group.getId(), invitation.getId())
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
        Group group = groupRepository.save(
                new Group("test group", "test description", "https://test.com", currentUser));
        GroupInvitation invitation = groupInvitationRepository.save(new GroupInvitation(group));
        GroupInvitationAcceptRequest request = new GroupInvitationAcceptRequest(invitation.getInvitationKey());

        // when
        ExtractableResponse<?> response = with()
                .body(request)
                .when()
                .request(POST, "/groups/{groupId}/invitations/{invitationId}/accept",
                        group.getId() + 3, invitation.getId())
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
        Group group = groupRepository.save(
                Group.builder()
                        .name("test group")
                        .description("test description")
                        .thumbnailUrl("https://test.com")
                        .creator(currentUser)
                        .build());
        assert challengeRepository.findAllByGroupId(group.getId()).isEmpty();

        ChallengeCreationRequest request = new ChallengeCreationRequest("test challenge",
                LocalDateTime.now().plusDays(3), "test description", List.of(
                new TaskRequest("test task 1", LocalDateTime.now().plusHours(3)),
                new TaskRequest("test task 2", LocalDateTime.now().plusHours(6)),
                new TaskRequest("test task 3", LocalDateTime.now().plusHours(9))
        ));

        // when
        ExtractableResponse<?> response = with()
                .body(request)
                .when().request(POST, "/groups/{groupId}/challenges", group.getId())
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(response.jsonPath().getString("data.id")).isNotBlank();
        assertThat(response.jsonPath().getString("data.title")).isEqualTo("test challenge");
        assertThat(response.jsonPath().getString("data.deadline")).isNotBlank();
        assertThat(response.jsonPath().getString("data.description")).isEqualTo("test description");
    }

    @Test
    @DisplayName("존재하지 않는 그룹에 챌린지를 등록하면 오류로 응답한다")
    void addChallengeToGroup_NotFound() {
        // given
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
                .when().request(POST, "/groups/{groupId}/challenges", 1L)
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
        Group group = groupRepository.save(
                Group.builder()
                        .name("test group")
                        .description("test description")
                        .thumbnailUrl("https://test.com")
                        .creator(currentUser)
                        .build());

        ChallengeCreationRequest request = new ChallengeCreationRequest("",
                LocalDateTime.now().plusDays(3), "test description", List.of(
                new TaskRequest("test task 1", LocalDateTime.now().plusHours(3)),
                new TaskRequest("test task 2", LocalDateTime.now().plusHours(6)),
                new TaskRequest("test task 3", LocalDateTime.now().plusHours(9))
        ));

        // when
        ExtractableResponse<?> response = with()
                .body(request)
                .when().request(POST, "/groups/{groupId}/challenges", group.getId())
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
        Group group = groupRepository.save(
                Group.builder()
                        .name("test group")
                        .description("test description")
                        .thumbnailUrl("https://test.com")
                        .creator(currentUser)
                        .build());

        ChallengeCreationRequest request = new ChallengeCreationRequest("a".repeat(101),
                LocalDateTime.now().plusDays(3), "test description", List.of(
                new TaskRequest("test task 1", LocalDateTime.now().plusHours(3)),
                new TaskRequest("test task 2", LocalDateTime.now().plusHours(6)),
                new TaskRequest("test task 3", LocalDateTime.now().plusHours(9))
        ));

        // when
        ExtractableResponse<?> response = with()
                .body(request)
                .when().request(POST, "/groups/{groupId}/challenges", group.getId())
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
        Group group = groupRepository.save(
                Group.builder()
                        .name("test group")
                        .description("test description")
                        .thumbnailUrl("https://test.com")
                        .creator(currentUser)
                        .build());

        ChallengeCreationRequest request = new ChallengeCreationRequest("test challenge",
                LocalDateTime.now().plusDays(3), "a".repeat(1001), List.of(
                new TaskRequest("test task 1", LocalDateTime.now().plusHours(3)),
                new TaskRequest("test task 2", LocalDateTime.now().plusHours(6)),
                new TaskRequest("test task 3", LocalDateTime.now().plusHours(9))
        ));

        // when
        ExtractableResponse<?> response = with()
                .body(request)
                .when().request(POST, "/groups/{groupId}/challenges", group.getId())
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
        Group group = groupRepository.save(
                Group.builder()
                        .name("test group")
                        .description("test description")
                        .thumbnailUrl("https://test.com")
                        .creator(currentUser)
                        .build());

        ChallengeCreationRequest request = new ChallengeCreationRequest("test challenge",
                LocalDateTime.now().minusDays(1), "test description", List.of(
                new TaskRequest("test task 1", LocalDateTime.now().plusHours(3)),
                new TaskRequest("test task 2", LocalDateTime.now().plusHours(6)),
                new TaskRequest("test task 3", LocalDateTime.now().plusHours(9))
        ));

        // when
        ExtractableResponse<?> response = with()
                .body(request)
                .when().request(POST, "/groups/{groupId}/challenges", group.getId())
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
        Group group = groupRepository.save(
                Group.builder()
                        .name("test group")
                        .description("test description")
                        .thumbnailUrl("https://test.com")
                        .creator(currentUser)
                        .build());

        ChallengeCreationRequest request = new ChallengeCreationRequest("test challenge",
                LocalDateTime.now().plusDays(3), "test description", List.of(
                new TaskRequest("", LocalDateTime.now().plusHours(3)),
                new TaskRequest("test task 2", LocalDateTime.now().plusHours(6)),
                new TaskRequest("test task 3", LocalDateTime.now().plusHours(9))
        ));

        // when
        ExtractableResponse<?> response = with()
                .body(request)
                .when().request(POST, "/groups/{groupId}/challenges", group.getId())
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
        Group group = groupRepository.save(
                Group.builder()
                        .name("test group")
                        .description("test description")
                        .thumbnailUrl("https://test.com")
                        .creator(currentUser)
                        .build());

        ChallengeCreationRequest request = new ChallengeCreationRequest("test challenge",
                LocalDateTime.now().plusDays(3), "test description", List.of(
                new TaskRequest("a".repeat(101), LocalDateTime.now().plusHours(3)),
                new TaskRequest("test task 2", LocalDateTime.now().plusHours(6)),
                new TaskRequest("test task 3", LocalDateTime.now().plusHours(9))
        ));

        // when
        ExtractableResponse<?> response = with()
                .body(request)
                .when().request(POST, "/groups/{groupId}/challenges", group.getId())
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
        Group group = groupRepository.save(
                Group.builder()
                        .name("test group")
                        .description("test description")
                        .thumbnailUrl("https://test.com")
                        .creator(currentUser)
                        .build());

        ChallengeCreationRequest request = new ChallengeCreationRequest("test challenge",
                LocalDateTime.now().plusDays(3), "test description", List.of(
                new TaskRequest("test task 1", LocalDateTime.now().plusHours(3)),
                new TaskRequest("test task 2", null),
                new TaskRequest("test task 3", LocalDateTime.now().plusHours(9))
        ));

        // when
        ExtractableResponse<?> response = with()
                .body(request)
                .when().request(POST, "/groups/{groupId}/challenges", group.getId())
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
        Group group = groupRepository.save(
                Group.builder()
                        .name("test group")
                        .description("test description")
                        .thumbnailUrl("https://test.com")
                        .creator(currentUser)
                        .build());

        ChallengeCreationRequest request = new ChallengeCreationRequest("test challenge",
                LocalDateTime.now().plusDays(3), "test description", List.of(
                new TaskRequest("test task 1", LocalDateTime.now().minusHours(3)),
                new TaskRequest("test task 2", LocalDateTime.now().plusHours(6)),
                new TaskRequest("test task 3", LocalDateTime.now().plusHours(9))
        ));

        // when
        ExtractableResponse<?> response = with()
                .body(request)
                .when().request(POST, "/groups/{groupId}/challenges", group.getId())
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
        Group group = groupRepository.save(
                new Group("test group", "test description", "https://test.com", currentUser));

        Challenge challenge1 = challengeRepository.save(new ChallengeCreationRequest("test challenge 1",
                LocalDateTime.now().plusDays(3), null, List.of(
                new TaskRequest("test task 1-1", LocalDateTime.now().plusHours(3)),
                new TaskRequest("test task 1-2", LocalDateTime.now().plusHours(6)),
                new TaskRequest("test task 1-3", LocalDateTime.now().plusHours(9))
        )).toEntity(currentUser, group));
        Challenge challenge2 = challengeRepository.save(new ChallengeCreationRequest("test challenge 2",
                LocalDateTime.now().plusDays(3), null, List.of(
                new TaskRequest("test task 2-1", LocalDateTime.now().plusHours(3))
        )).toEntity(currentUser, group));
        Challenge challenge3 = challengeRepository.save(new ChallengeCreationRequest("test challenge 3",
                LocalDateTime.now().plusDays(3), null, List.of(
                new TaskRequest("test task 3-1", LocalDateTime.now().plusHours(3)),
                new TaskRequest("test task 3-2", LocalDateTime.now().plusHours(6)),
                new TaskRequest("test task 3-3", LocalDateTime.now().plusHours(9)),
                new TaskRequest("test task 3-4", LocalDateTime.now().plusHours(12))
        )).toEntity(currentUser, group));

        // when
        ExtractableResponse<?> response = with()
                .when().request(GET, "/groups/{groupId}/challenges", group.getId())
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(response.jsonPath().getList("data.challenges")).hasSize(3);
        assertThat(response.jsonPath().getString("data.challenges[0].title")).isEqualTo("test challenge 1");
        assertThat(response.jsonPath().getString("data.challenges[1].title")).isEqualTo("test challenge 2");
        assertThat(response.jsonPath().getString("data.challenges[2].title")).isEqualTo("test challenge 3");
    }

    @Test
    @DisplayName("그룹의 테스크 목록을 조회한다")
    void getTasks_Success() throws MalformedURLException {
        // given
        Group group = groupRepository.save(
                new Group("test group", "test description", "https://test.com", currentUser));

        Challenge challenge1 = challengeRepository.save(new ChallengeCreationRequest("test challenge 1",
                LocalDateTime.now().plusDays(3), null, List.of(
                new TaskRequest("test task 1-1", LocalDateTime.now().plusHours(3)),
                new TaskRequest("test task 1-2", LocalDateTime.now().plusHours(6)),
                new TaskRequest("test task 1-3", LocalDateTime.now().plusHours(9))
        )).toEntity(currentUser, group));
        Challenge challenge2 = challengeRepository.save(new ChallengeCreationRequest("test challenge 2",
                LocalDateTime.now().plusDays(3), null, List.of(
                new TaskRequest("test task 2-1", LocalDateTime.now().plusHours(3))
        )).toEntity(currentUser, group));
        Challenge challenge3 = challengeRepository.save(new ChallengeCreationRequest("test challenge 3",
                LocalDateTime.now().plusDays(3), null, List.of(
                new TaskRequest("test task 3-1", LocalDateTime.now().plusHours(3)),
                new TaskRequest("test task 3-2", LocalDateTime.now().plusHours(6)),
                new TaskRequest("test task 3-3", LocalDateTime.now().plusHours(9)),
                new TaskRequest("test task 3-4", LocalDateTime.now().plusHours(12))
        )).toEntity(currentUser, group));

        challenge1.getTasks().get(0).certify(List.of(), List.of(new URL("https://test.com")));
        challenge2.getTasks().get(0)
                .certify(List.of(new URL("https://test.com"), new URL("https://test2.com")),
                        List.of(new URL("https://test.com")));
        challenge3.getTasks().get(2).certify(List.of(new URL("https://test.com")), List.of());
        challengeRepository.saveAll(List.of(challenge1, challenge2, challenge3));

        // when
        ExtractableResponse<?> response = with()
                .when().request(GET, "/groups/{groupId}/tasks", group.getId())
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(response.jsonPath().getList("data.tasks")).hasSize(8);
        assertThat(response.jsonPath().getList("data.tasks", GroupTaskListItem.class))
                .anySatisfy(task -> assertThat(task.certification()).isNotNull());
        assertThat(response.jsonPath().getList("data.tasks", GroupTaskListItem.class))
                .anySatisfy(task -> assertThat(task.certification()).isNull());
    }

    @Test
    @DisplayName("그룹의 채팅 메시지를 페이지네이션하여 조회한다")
    void getMessages_Success() {
        // given
        Group group = groupRepository.save(
                new Group("test group", "test description", "https://test.com", currentUser));

        LocalDateTime now = LocalDateTime.now();
        List<ChatMessage> messages = List.of(
                new ChatMessage(currentUser.getId(), group.getId(), "첫 번째 메시지", now.plusSeconds(1)),
                new ChatMessage(currentUser.getId(), group.getId(), "두 번째 메시지", now.plusSeconds(2)),
                new ChatMessage(currentUser.getId(), group.getId(), "세 번째 메시지", now.plusSeconds(3))
        );
        chatMessageRepository.saveAll(messages);

        // when
        ExtractableResponse<?> response = with()
                .when().request(GET, "/groups/{groupId}/messages?page=0&size=2", group.getId())
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(response.jsonPath().getList("data.messages")).hasSize(2);
        assertThat(response.jsonPath().getInt("data.currentPage")).isEqualTo(0);
        assertThat(response.jsonPath().getInt("data.totalPages")).isEqualTo(2);
        assertThat(response.jsonPath().getLong("data.totalElements")).isEqualTo(3);

        List<String> contents = response.jsonPath().getList("data.messages.content");
        assertThat(contents).containsExactly("세 번째 메시지", "두 번째 메시지"); // 최신순 정렬 확인
    }

    @Test
    @DisplayName("존재하지 않는 그룹의 채팅 메시지를 조회하면 오류로 응답한다")
    void getMessages_EmptyGroup() {
        // given
        assert groupRepository.findAll().isEmpty();
        assert chatMessageRepository.findAll().isEmpty();

        // when
        ExtractableResponse<?> response = with()
                .when().request(GET, "/groups/{groupId}/messages", 1L)
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_NOT_FOUND);
    }

    @Test
    @DisplayName("채팅 메시지가 없는 그룹을 조회하면 빈 목록을 반환한다")
    void getMessages_EmptyMessages() {
        // given
        Group group = groupRepository.save(
                new Group("test group", "test description", "https://test.com", currentUser));
        assert chatMessageRepository.findAll().isEmpty();

        // when
        ExtractableResponse<?> response = with()
                .when().request(GET, "/groups/{groupId}/messages", group.getId())
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(response.jsonPath().getList("data.messages")).isEmpty();
        assertThat(response.jsonPath().getInt("data.currentPage")).isEqualTo(0);
        assertThat(response.jsonPath().getInt("data.totalPages")).isEqualTo(0);
        assertThat(response.jsonPath().getLong("data.totalElements")).isEqualTo(0);
    }
}
