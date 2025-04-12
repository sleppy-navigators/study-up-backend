package sleppynavigators.studyupbackend.presentation.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sleppynavigators.studyupbackend.application.user.UserService;
import sleppynavigators.studyupbackend.presentation.authentication.filter.UserPrincipal;
import sleppynavigators.studyupbackend.presentation.challenge.dto.request.TaskSearch;
import sleppynavigators.studyupbackend.presentation.common.SearchParam;
import sleppynavigators.studyupbackend.presentation.common.SuccessResponse;
import sleppynavigators.studyupbackend.presentation.group.dto.request.GroupSearch;
import sleppynavigators.studyupbackend.presentation.group.dto.response.GroupListResponse;
import sleppynavigators.studyupbackend.presentation.user.dto.response.UserResponse;
import sleppynavigators.studyupbackend.presentation.user.dto.response.UserTaskListResponse;

@Tag(name = "User", description = "유저 관련 API")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    @Operation(summary = "유저 정보 조회", description = "유저 정보를 조회합니다.")
    public ResponseEntity<SuccessResponse<UserResponse>> getUserInfo(@PathVariable Long userId) {
        UserResponse response = userService.getUser(userId);
        return ResponseEntity.ok(new SuccessResponse<>(response));
    }

    @GetMapping("/me/groups")
    @Operation(summary = "유저의 그룹 목록 조회", description = "유저의 그룹 목록을 조회합니다.")
    public ResponseEntity<SuccessResponse<GroupListResponse>> getGroups(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @SearchParam @Valid GroupSearch groupSearch) {
        Long userId = userPrincipal.userId();
        GroupListResponse response = userService.getGroups(userId, groupSearch);
        return ResponseEntity.ok(new SuccessResponse<>(response));
    }

    @GetMapping("/me/tasks")
    @Operation(summary = "유저의 테스크 목록 조회", description = "유저의 테스크 목록을 조회합니다.")
    public ResponseEntity<SuccessResponse<UserTaskListResponse>> getTasks(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @SearchParam @Valid TaskSearch taskSearch
    ) {
        Long userId = userPrincipal.userId();
        UserTaskListResponse response = userService.getTasks(userId, taskSearch);
        return ResponseEntity.ok(new SuccessResponse<>(response));
    }
}
