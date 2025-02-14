package sleppynavigators.studyupbackend.presentation.group;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sleppynavigators.studyupbackend.application.group.GroupService;
import sleppynavigators.studyupbackend.presentation.authentication.filter.UserPrincipal;
import sleppynavigators.studyupbackend.presentation.common.SuccessResponse;
import sleppynavigators.studyupbackend.presentation.group.dto.request.GroupCreationRequest;
import sleppynavigators.studyupbackend.presentation.group.dto.response.GroupInvitationResponse;
import sleppynavigators.studyupbackend.presentation.group.dto.response.GroupListResponse;
import sleppynavigators.studyupbackend.presentation.group.dto.response.SimpleGroupResponse;

@Tag(name = "Group", description = "그룹 관련 API")
@RestController
@RequestMapping("/groups")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupController {

    private final GroupService groupService;

    @GetMapping
    @Operation(summary = "그룹 목록 조회", description = "사용자의 그룹 목록을 조회합니다.")
    public ResponseEntity<SuccessResponse<GroupListResponse>> getGroups(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long userId = userPrincipal.userId();
        GroupListResponse response = groupService.getGroups(userId);
        return ResponseEntity.ok(new SuccessResponse<>(response));
    }

    @PostMapping
    @Operation(summary = "그룹 생성", description = "그룹을 생성합니다.")
    public ResponseEntity<SuccessResponse<SimpleGroupResponse>> createGroup(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody @Valid GroupCreationRequest groupCreationRequest
    ) {
        Long userId = userPrincipal.userId();
        SimpleGroupResponse response = groupService.createGroup(userId, groupCreationRequest);
        return ResponseEntity.ok(new SuccessResponse<>(response));
    }

    @PostMapping("/{groupId}/leave")
    @Operation(summary = "그룹 탈퇴", description = "그룹에서 탈퇴합니다.")
    public ResponseEntity<SuccessResponse<Void>> leaveGroup(
            @AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long groupId) {
        Long userId = userPrincipal.userId();
        groupService.leaveGroup(userId, groupId);
        return ResponseEntity.ok(new SuccessResponse<>(null));
    }

    @PostMapping("/:groupId/invitations")
    @Operation(summary = "그룹 초대", description = "그룹에 사용자를 초대합니다.")
    public ResponseEntity<SuccessResponse<GroupInvitationResponse>> inviteUser(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return null;
    }

    @GetMapping("/:groupId/invitations/:invitationId")
    @Operation(summary = "그룹 초대 조회", description = "그룹 초대를 조회합니다.")
    public ResponseEntity<SuccessResponse<SimpleGroupResponse>> getInvitation() {
        return null;
    }

    @PostMapping("/:groupId/invitations/:invitationId/accept")
    @Operation(summary = "그룹 초대 수락", description = "그룹 초대를 수락합니다.")
    public ResponseEntity<SuccessResponse<SimpleGroupResponse>> acceptInvitation(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return null;
    }
}
