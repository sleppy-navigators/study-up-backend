package sleppynavigators.studyupbackend.presentation.user;

import static io.restassured.RestAssured.with;
import static io.restassured.http.Method.GET;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.ExtractableResponse;

import java.util.List;
import java.util.Objects;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sleppynavigators.studyupbackend.common.RestAssuredBaseTest;
import sleppynavigators.studyupbackend.common.support.AuthSupport;
import sleppynavigators.studyupbackend.common.support.ChallengeSupport;
import sleppynavigators.studyupbackend.common.support.GroupSupport;
import sleppynavigators.studyupbackend.common.support.UserSupport;
import sleppynavigators.studyupbackend.domain.challenge.Challenge;
import sleppynavigators.studyupbackend.domain.group.Group;
import sleppynavigators.studyupbackend.domain.user.User;
import sleppynavigators.studyupbackend.presentation.challenge.dto.response.TaskGroupDTO;
import sleppynavigators.studyupbackend.presentation.group.dto.response.GroupListResponse;
import sleppynavigators.studyupbackend.presentation.group.dto.response.GroupListResponse.GroupListItem;
import sleppynavigators.studyupbackend.presentation.user.dto.response.UserResponse;
import sleppynavigators.studyupbackend.presentation.user.dto.response.UserTaskListResponse;
import sleppynavigators.studyupbackend.presentation.user.dto.response.UserTaskListResponse.UserTaskListItem;

@DisplayName("UserController API 테스트")
public class UserControllerTest extends RestAssuredBaseTest {

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
    @DisplayName("사용자 정보 조회에 성공한다")
    void getUserInfo_Success() {
        // given
        User userToQuery = userSupport.registerUserToDB();

        // when
        ExtractableResponse<?> response = with()
                .when().request(GET, "/users/{userId}", userToQuery.getId())
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(response.jsonPath().getObject("data", UserResponse.class))
                .satisfies(data -> {
                    assertThat(this.validator.validate(data)).isEmpty();
                    assertThat(data.id()).isEqualTo(userToQuery.getId());
                });
    }

    @Test
    @DisplayName("사용자가 그룹 목록 조회에 성공한다")
    void getGroups_Success() {
        // given
        User anotherUser1 = userSupport.registerUserToDB();
        User anotherUser2 = userSupport.registerUserToDB();
        User anotherUser3 = userSupport.registerUserToDB();

        Group group1 = groupSupport.callToMakeGroup(List.of(currentUser));
        Group group2 = groupSupport.callToMakeGroup(List.of(currentUser, anotherUser1));
        Group group3 = groupSupport.callToMakeGroup(List.of(currentUser, anotherUser2, anotherUser3));

        Challenge challenge1 = challengeSupport
                .callToMakeChallengesWithTasks(group1, 3, 2, currentUser);
        Challenge challenge2 = challengeSupport
                .callToMakeChallengesWithTasks(group2, 4, 0, currentUser);

        challengeSupport.callToCancelChallenge(currentUser, challenge1);

        // when
        ExtractableResponse<?> response = with()
                .when().request(GET, "/users/me/groups")
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(response.jsonPath().getObject("data", GroupListResponse.class))
                .satisfies(data -> {
                    assertThat(this.validator.validate(data)).isEmpty();
                    assertThat(data.groups()).hasSize(3);
                    assertThat(data.groups()).map(GroupListItem::numOfMembers)
                            .containsExactly(1, 2, 3);
                    assertThat(data.groups()).map(GroupListItem::lastChatMessage)
                            .containsExactly(
                                    "test-user님이 'test-challenge' 챌린지를 취소했습니다.",
                                    "test-user님이 'test-challenge' 챌린지를 생성했습니다.",
                                    "test-user님이 그룹에 참여했습니다.");
                });
    }

    @Test
    @DisplayName("사용자가 테스크 목록 조회에 성공한다")
    void getTasks_Success() {
        // given
        User anotherUser1 = userSupport.registerUserToDB();
        User anotherUser2 = userSupport.registerUserToDB();

        Group groupCurrentlyJoined = groupSupport.callToMakeGroup(List.of(currentUser, anotherUser1));
        Group groupWillNotJoined = groupSupport.callToMakeGroup(List.of(currentUser, anotherUser2));

        Challenge challenge1 = challengeSupport
                .callToMakeChallengesWithTasks(groupCurrentlyJoined, 3, 2, currentUser);
        Challenge challenge2 = challengeSupport
                .callToMakeChallengesWithTasks(groupCurrentlyJoined, 4, 0, currentUser);
        Challenge challenge3 = challengeSupport
                .callToMakeChallengesWithTasks(groupWillNotJoined, 2, 2, currentUser);

        // when
        groupSupport.callToLeaveGroup(currentUser, groupWillNotJoined.getId());
        ExtractableResponse<?> response = with()
                .when().request(GET, "/users/me/tasks")
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(response.jsonPath().getObject("data", UserTaskListResponse.class))
                .satisfies(data -> {
                    assertThat(this.validator.validate(data)).isEmpty();
                    assertThat(data.tasks()).hasSize(9);
                    assertThat(data.tasks()).map(UserTaskListItem::certification).anyMatch(Objects::nonNull);
                    assertThat(data.tasks()).map(UserTaskListItem::certification).anyMatch(Objects::isNull);
                    assertThat(data.tasks())
                            .map(UserTaskListItem::groupDetail)
                            .map(TaskGroupDTO::currentlyJoined)
                            .containsExactly(true, true, true, true, true, true, true, false, false);
                });
    }
}
