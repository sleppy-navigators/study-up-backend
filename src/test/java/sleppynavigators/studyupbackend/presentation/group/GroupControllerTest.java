package sleppynavigators.studyupbackend.presentation.group;

import static io.restassured.RestAssured.with;
import static io.restassured.http.Method.POST;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import sleppynavigators.studyupbackend.domain.user.User;
import sleppynavigators.studyupbackend.domain.user.vo.UserProfile;
import sleppynavigators.studyupbackend.infrastructure.group.GroupRepository;
import sleppynavigators.studyupbackend.infrastructure.user.UserRepository;
import sleppynavigators.studyupbackend.presentation.common.SuccessResponse;
import sleppynavigators.studyupbackend.presentation.common.WithMockedUserInfo;
import sleppynavigators.studyupbackend.presentation.group.dto.GroupCreationRequest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@WithMockedUserInfo
@DisplayName("GroupController 테스트")
public class GroupControllerTest {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        userRepository.save(new User(new UserProfile("guest", "example@guest.com")));

        RestAssured.port = port;
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .build();
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        groupRepository.deleteAll();
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
}
