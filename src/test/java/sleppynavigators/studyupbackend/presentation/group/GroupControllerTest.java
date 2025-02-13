package sleppynavigators.studyupbackend.presentation.group;

import static io.restassured.RestAssured.with;
import static io.restassured.http.Method.POST;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import java.util.List;
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
import sleppynavigators.studyupbackend.domain.group.Group;
import sleppynavigators.studyupbackend.domain.user.User;
import sleppynavigators.studyupbackend.domain.user.vo.UserProfile;
import sleppynavigators.studyupbackend.infrastructure.group.GroupRepository;
import sleppynavigators.studyupbackend.infrastructure.user.UserRepository;
import sleppynavigators.studyupbackend.presentation.common.SuccessResponse;
import sleppynavigators.studyupbackend.presentation.group.dto.GroupCreationRequest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("GroupController API 테스트")
public class GroupControllerTest {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccessTokenProperties accessTokenProperties;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        UserProfile userProfile = new UserProfile("guest", "example@guest.com");
        User savedUser = userRepository.saveAndFlush(new User(userProfile));

        AccessToken accessToken =
                new AccessToken(savedUser.getId(), userProfile, List.of("profile"), accessTokenProperties);
        String bearerToken = "Bearer " + accessToken.serialize(accessTokenProperties);

        RestAssured.port = port;
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .addHeader("Authorization", bearerToken)
                .build();
    }

    @AfterEach
    void tearDown() {
        groupRepository.deleteAll();
        userRepository.deleteAll();
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
        assertThat(response.body().as(SuccessResponse.class).getData()).isNotNull();
        assertThat(groupRepository.findAll()).hasSize(1);
    }

    @Test
    @DisplayName("사용자가 그룹에서 탈퇴에 성공한다")
    void memberLeaveGroup_Success() {        // given
        User creator = userRepository.findByUserProfileEmail("example@guest.com").orElseThrow();
        User anotherMember = userRepository.save(new User(new UserProfile("another", "example2@guest.com")));

        Group group = new Group("test group", "test description", "https://test.com", creator);
        group.addMember(anotherMember);
        Group savedGroup = groupRepository.saveAndFlush(group);

        // when
        ExtractableResponse<?> response = with()
                .when().request(POST, "/groups/{groupId}/withdraw", savedGroup.getId())
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(response.body().as(SuccessResponse.class).getData()).isNull();
        assertThat(groupRepository.findAll()).isNotEmpty();
    }

    @Test
    @DisplayName("유일한 사용자가 그룹에서 탈퇴한다 (그룹이 삭제된다)")
    void uniqueMemberLeaveGroup_Success() {
        // given
        User creator = userRepository.findByUserProfileEmail("example@guest.com").orElseThrow();
        Group group = new Group("test group", "test description", "https://test.com", creator);
        Group savedGroup = groupRepository.saveAndFlush(group);

        // when
        ExtractableResponse<?> response = with()
                .when().request(POST, "/groups/{groupId}/withdraw", savedGroup.getId())
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(response.body().as(SuccessResponse.class).getData()).isNull();
        assertThat(groupRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 그룹에 탈퇴를 요청하면 오류로 응답한다")
    void leaveGroup_NotFound() {
        // given
        assert groupRepository.findAll().isEmpty();

        // when
        ExtractableResponse<?> response = with()
                .when().request(POST, "/groups/{groupId}/withdraw", 1L)
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_BAD_REQUEST);
    }
}
