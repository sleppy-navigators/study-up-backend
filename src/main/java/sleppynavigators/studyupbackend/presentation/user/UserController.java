package sleppynavigators.studyupbackend.presentation.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sleppynavigators.studyupbackend.application.user.UserService;
import sleppynavigators.studyupbackend.presentation.authentication.filter.UserPrincipal;
import sleppynavigators.studyupbackend.presentation.common.SuccessResponse;
import sleppynavigators.studyupbackend.presentation.group.dto.response.GroupListResponse;
import sleppynavigators.studyupbackend.presentation.user.dto.response.UserTaskListResponse;

@Tag(name = "User", description = "유저 관련 API")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class UserController {

    private final UserService userService;

    @GetMapping("/me/groups")
    @Operation(summary = "유저의 그룹 목록 조회", description = "유저의 그룹 목록을 조회합니다.")
    public ResponseEntity<SuccessResponse<GroupListResponse>> getGroups(
            // TODO: sort by `Event`(challenge creation and task certification) utilizing `@SortDefault`
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long userId = userPrincipal.userId();
        GroupListResponse response = userService.getGroups(userId);
        return ResponseEntity.ok(new SuccessResponse<>(response));
    }

    @GetMapping("/me/tasks")
    @Operation(summary = "유저의 테스크 목록 조회", description = "유저의 테스크 목록을 조회합니다.")
    public ResponseEntity<SuccessResponse<UserTaskListResponse>> getTasks(
            // TODO: filter by deadline utilizing `RSQL` or `QueryDSL Web Support`
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        Long userId = userPrincipal.userId();
        UserTaskListResponse response = userService.getTasks(userId);
        return ResponseEntity.ok(new SuccessResponse<>(response));
    }
}
