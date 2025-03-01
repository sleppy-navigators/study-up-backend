package sleppynavigators.studyupbackend.presentation.group;

import static io.restassured.RestAssured.with;
import static io.restassured.http.Method.GET;
import static io.restassured.http.Method.POST;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
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
import sleppynavigators.studyupbackend.domain.chat.ChatMessage;
import sleppynavigators.studyupbackend.domain.group.Group;
import sleppynavigators.studyupbackend.domain.group.invitation.GroupInvitation;
import sleppynavigators.studyupbackend.domain.user.User;
import sleppynavigators.studyupbackend.domain.user.vo.UserProfile;
import sleppynavigators.studyupbackend.exception.ErrorCode;
import sleppynavigators.studyupbackend.infrastructure.chat.ChatMessageRepository;
import sleppynavigators.studyupbackend.infrastructure.group.GroupRepository;
import sleppynavigators.studyupbackend.infrastructure.group.invitation.GroupInvitationRepository;
import sleppynavigators.studyupbackend.infrastructure.user.UserRepository;
import sleppynavigators.studyupbackend.presentation.common.DatabaseCleaner;
import sleppynavigators.studyupbackend.presentation.group.dto.request.GroupCreationRequest;
import sleppynavigators.studyupbackend.presentation.group.dto.request.GroupInvitationAcceptRequest;

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
    @DisplayName("사용자가 그룹 목록 조회에 성공한다")
    void getGroups_Success() {
        // given
        groupRepository.saveAll(
                List.of(new Group("test group1", "test description", "https://test.com", currentUser),
                        new Group("test group2", "test description", "https://test.com", currentUser),
                        new Group("test group3", "test description", "https://test.com", currentUser),
                        new Group("test group4", "test description", "https://test.com", currentUser)));

        // when
        ExtractableResponse<?> response = with()
                .when().request(GET, "/groups")
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(response.jsonPath().getList("data.groups")).hasSize(4);
        assertThat(response.jsonPath().getString("data.groups[].id")).isNotBlank();
        assertThat(response.jsonPath().getString("data.groups[].name")).isNotBlank();
        assertThat(response.jsonPath().getString("data.groups[].description")).isNotBlank();
        assertThat(response.jsonPath().getString("data.groups[].thumbnailUrl")).isNotBlank();
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
