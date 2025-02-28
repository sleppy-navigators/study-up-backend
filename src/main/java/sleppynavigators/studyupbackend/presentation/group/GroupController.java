package sleppynavigators.studyupbackend.presentation.group;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sleppynavigators.studyupbackend.application.chat.ChatMessageService;
import sleppynavigators.studyupbackend.application.group.GroupService;
import sleppynavigators.studyupbackend.presentation.authentication.filter.UserPrincipal;
import sleppynavigators.studyupbackend.presentation.chat.dto.response.ChatMessageListResponse;
import sleppynavigators.studyupbackend.presentation.common.SuccessResponse;
import sleppynavigators.studyupbackend.presentation.group.dto.request.GroupCreationRequest;
import sleppynavigators.studyupbackend.presentation.group.dto.request.GroupInvitationAcceptRequest;
import sleppynavigators.studyupbackend.presentation.group.dto.response.GroupInvitationResponse;
import sleppynavigators.studyupbackend.presentation.group.dto.response.GroupResponse;

@Tag(name = "Group", description = "그룹 관련 API")
@RestController
@RequestMapping("/groups")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupController {

    private final GroupService groupService;
    private final ChatMessageService chatMessageService;

    @PostMapping
    @Operation(summary = "그룹 생성", description = "그룹을 생성합니다.")
    public ResponseEntity<SuccessResponse<GroupResponse>> createGroup(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody @Valid GroupCreationRequest groupCreationRequest
    ) {
        Long userId = userPrincipal.userId();
        GroupResponse response = groupService.createGroup(userId, groupCreationRequest);
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

    @PostMapping("/{groupId}/invitations")
    @Operation(summary = "그룹 초대", description = "그룹에 사용자를 초대합니다.")
    public ResponseEntity<SuccessResponse<GroupInvitationResponse>> inviteUser(
            @AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long groupId) {
        GroupInvitationResponse response = groupService.makeInvitation(groupId, userPrincipal.userId());
        return ResponseEntity.ok(new SuccessResponse<>(response));
    }

    @GetMapping("/{groupId}/invitations/{invitationId}")
    @Operation(summary = "그룹 초대 조회", description = "그룹 초대를 조회합니다.")
    public ResponseEntity<SuccessResponse<GroupResponse>> getInvitation(
            @PathVariable Long groupId, @PathVariable Long invitationId
    ) {
        GroupResponse response = groupService.getInvitedGroup(groupId, invitationId);
        return ResponseEntity.ok(new SuccessResponse<>(response));
    }

    @PostMapping("/{groupId}/invitations/{invitationId}/accept")
    @Operation(summary = "그룹 초대 수락", description = "그룹 초대를 수락합니다.")
    public ResponseEntity<SuccessResponse<GroupResponse>> acceptInvitation(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long groupId, @PathVariable Long invitationId,
            @RequestBody @Valid GroupInvitationAcceptRequest groupInvitationAcceptRequest) {
        Long userId = userPrincipal.userId();
        GroupResponse response =
                groupService.acceptInvitation(userId, groupId, invitationId, groupInvitationAcceptRequest);
        return ResponseEntity.ok(new SuccessResponse<>(response));
    }

    @GetMapping("/{groupId}/messages")
    @Operation(summary = "그룹 채팅 메시지 조회", description = "그룹의 채팅 메시지를 페이지네이션하여 조회합니다.")
    public ResponseEntity<SuccessResponse<ChatMessageListResponse>> getMessages(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long groupId,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        ChatMessageListResponse response = chatMessageService.getMessages(groupId, pageable);
        return ResponseEntity.ok(new SuccessResponse<>(response));
    }
}
