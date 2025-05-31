package sleppynavigators.studyupbackend.presentation.user;

import static io.restassured.RestAssured.with;
import static io.restassured.http.Method.DELETE;
import static io.restassured.http.Method.GET;
import static io.restassured.http.Method.POST;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.ExtractableResponse;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sleppynavigators.studyupbackend.application.challenge.TaskCertificationStatus;
import sleppynavigators.studyupbackend.application.group.GroupSortType;
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
import sleppynavigators.studyupbackend.presentation.user.dto.response.FollowerListResponse;
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
    @DisplayName("최초 회원가입 시, 사용자는 1,000 포인트를 보유하고 있다")
    void getUserInfo_Success_InitialPoint() {
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
                    assertThat(data.point()).isEqualTo(1000);
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
                    assertThat(data.groups()).map(GroupListItem::memberCount)
                            .containsExactly(1, 2, 3);
                    assertThat(data.groups()).map(GroupListItem::lastChatMessage)
                            .containsExactly(
                                    "test-user님이 'test-challenge' 챌린지를 취소했습니다.",
                                    "test-user님이 'test-challenge' 챌린지를 생성했습니다.",
                                    "test-user님이 그룹에 참여했습니다.");
                });
    }

    @Test
    @DisplayName("사용자가 그룹 목록 조회 - 시스템 메시지 순 정렬")
    void getGroups_Success_SortBySystemMessage() {
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

        // when
        ExtractableResponse<?> response = with()
                .queryParam("sortBy", GroupSortType.LATEST_CHAT)
                .when().request(GET, "/users/me/groups")
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(response.jsonPath().getObject("data", GroupListResponse.class))
                .satisfies(data -> {
                    assertThat(this.validator.validate(data)).isEmpty();
                    assertThat(data.groups()).hasSize(3);
                    assertThat(data.groups()).map(GroupListItem::memberCount)
                            .containsExactly(2, 1, 3);
                    assertThat(data.groups()).map(GroupListItem::lastChatMessage)
                            .containsExactly(
                                    "test-user님이 'test-challenge' 챌린지를 생성했습니다.",
                                    "test-user님이 'test-challenge' 테스크를 완료했습니다. (test-task-1)",
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
                .callToMakeCompletedChallengeWithTasks(groupWillNotJoined, 2, currentUser);

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

    @Test
    @DisplayName("사용자가 테스크 목록 조회 - 진행중인 테스크만")
    void getTasks_Certified_Success() {
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
                .callToMakeCompletedChallengeWithTasks(groupWillNotJoined, 2, currentUser);

        // when
        groupSupport.callToLeaveGroup(currentUser, groupWillNotJoined.getId());
        ExtractableResponse<?> response = with()
                .queryParam("status", TaskCertificationStatus.IN_PROGRESS)
                .when().request(GET, "/users/me/tasks")
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(response.jsonPath().getObject("data", UserTaskListResponse.class))
                .satisfies(data -> {
                    assertThat(this.validator.validate(data)).isEmpty();
                    assertThat(data.tasks()).hasSize(5);
                    assertThat(data.tasks()).map(UserTaskListItem::certification).allMatch(Objects::isNull);
                    assertThat(data.tasks())
                            .map(UserTaskListItem::groupDetail)
                            .map(TaskGroupDTO::currentlyJoined)
                            .containsExactly(true, true, true, true, true);
                });
    }

    @Test
    @DisplayName("사용자 팔로우 목록 조회에 성공한다")
    void getFollowerList_Success() {
        // given
        User following1 = userSupport.registerUserToDB();
        User following2 = userSupport.registerUserToDB();
        User following3 = userSupport.registerUserToDB();

        User follower1 = userSupport.registerUserToDB();
        User follower2 = userSupport.registerUserToDB();

        userSupport.callToFollowUser(currentUser, following1);
        userSupport.callToFollowUser(currentUser, following2);
        userSupport.callToFollowUser(currentUser, following3);

        userSupport.callToFollowUser(follower1, currentUser);
        userSupport.callToFollowUser(follower2, currentUser);

        // when
        ExtractableResponse<?> response = with()
                .when().request(GET, "/users/me/followers")
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(response.jsonPath().getObject("data", FollowerListResponse.class))
                .satisfies(data -> {
                    assertThat(this.validator.validate(data)).isEmpty();
                    assertThat(data.followings()).hasSize(3);
                    assertThat(data.followers()).hasSize(2);
                });
    }

    @Test
    @DisplayName("사용자 팔로우에 성공한다")
    void followUser_Success() {
        // given
        User userToFollow = userSupport.registerUserToDB("follow-user", "follow-email");

        // when
        ExtractableResponse<?> response = with()
                .when().request(POST, "/users/me/followers/{followeeId}", userToFollow.getId())
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(Optional.ofNullable(response.jsonPath().get("data"))).isEmpty();
        assertThat(userSupport.getFollowerListResponse(currentUser))
                .satisfies(data -> {
                    assertThat(data.followings()).hasSize(1);
                    assertThat(data.followings().get(0).id()).isEqualTo(userToFollow.getId());
                    assertThat(data.followers()).isEmpty();
                });
    }

    @Test
    @DisplayName("사용자 팔로우 - 이미 팔로우한 사용자")
    void followUser_AlreadyFollowed() {
        // given
        User userToFollow = userSupport.registerUserToDB("follow-user", "follow-email");
        userSupport.callToFollowUser(currentUser, userToFollow);

        // when
        ExtractableResponse<?> response = with()
                .when().request(POST, "/users/me/followers/{followeeId}", userToFollow.getId())
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(Optional.ofNullable(response.jsonPath().get("data"))).isEmpty();
        assertThat(userSupport.getFollowerListResponse(currentUser))
                .satisfies(data -> {
                    assertThat(data.followings()).hasSize(1);
                    assertThat(data.followings().get(0).id()).isEqualTo(userToFollow.getId());
                    assertThat(data.followers()).isEmpty();
                });
    }

    @Test
    @DisplayName("사용자 언팔로우에 성공한다")
    void unfollowUser_Success() {
        // given
        User userToUnfollow = userSupport.registerUserToDB("unfollow-user", "unfollow-email");
        userSupport.callToFollowUser(currentUser, userToUnfollow);

        // when
        ExtractableResponse<?> response = with()
                .when().request(DELETE, "/users/me/followers/{followeeId}", userToUnfollow.getId())
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(Optional.ofNullable(response.jsonPath().get("data"))).isEmpty();
        assertThat(userSupport.getFollowerListResponse(currentUser))
                .satisfies(data -> {
                    assertThat(data.followings()).isEmpty();
                    assertThat(data.followers()).isEmpty();
                });
    }

    @Test
    @DisplayName("사용자 언팔로우 - 팔로우하지 않은 사용자")
    void unfollowUser_NotFollowed() {
        // given
        User userToUnfollow = userSupport.registerUserToDB("unfollow-user", "unfollow-email");

        // when
        ExtractableResponse<?> response = with()
                .when().request(DELETE, "/users/me/followers/{followeeId}", userToUnfollow.getId())
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(Optional.ofNullable(response.jsonPath().get("data"))).isEmpty();
        assertThat(userSupport.getFollowerListResponse(currentUser))
                .satisfies(data -> {
                    assertThat(data.followings()).isEmpty();
                    assertThat(data.followers()).isEmpty();
                });
    }

    @Test
    @DisplayName("다른 사용자가 나를 팔로우했을 때, 팔로워 목록에 포함된다")
    void getFollowerList_IncludesOtherUser() {
        // given
        User anotherUser = userSupport.registerUserToDB("another-user", "another-email");

        // when
        ExtractableResponse<?> response = with()
                .when().request(POST, "/users/me/followers/{followeeId}", anotherUser.getId())
                .then()
                .log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(userSupport.getFollowerListResponse(anotherUser))
                .satisfies(data -> {
                    assertThat(data.followings()).isEmpty();
                    assertThat(data.followers()).hasSize(1);
                    assertThat(data.followers().get(0).id()).isEqualTo(currentUser.getId());
                });
    }
}
