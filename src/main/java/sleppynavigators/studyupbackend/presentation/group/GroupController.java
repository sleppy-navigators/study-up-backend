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
import sleppynavigators.studyupbackend.application.challenge.ChallengeService;
import sleppynavigators.studyupbackend.application.chat.ChatMessageService;
import sleppynavigators.studyupbackend.application.group.GroupService;
import sleppynavigators.studyupbackend.presentation.authentication.filter.UserPrincipal;
import sleppynavigators.studyupbackend.presentation.challenge.dto.request.ChallengeCreationRequest;
import sleppynavigators.studyupbackend.presentation.challenge.dto.request.ChallengeSearch;
import sleppynavigators.studyupbackend.presentation.challenge.dto.request.TaskSearch;
import sleppynavigators.studyupbackend.presentation.challenge.dto.response.ChallengeResponse;
import sleppynavigators.studyupbackend.presentation.chat.dto.request.ChatMessageSearch;
import sleppynavigators.studyupbackend.presentation.chat.dto.response.ChatMessageListResponse;
import sleppynavigators.studyupbackend.presentation.common.SuccessResponse;
import sleppynavigators.studyupbackend.presentation.common.argument.SearchParam;
import sleppynavigators.studyupbackend.presentation.group.dto.request.GroupCreationRequest;
import sleppynavigators.studyupbackend.presentation.group.dto.request.GroupInvitationAcceptRequest;
import sleppynavigators.studyupbackend.presentation.group.dto.request.GroupMemberSearch;
import sleppynavigators.studyupbackend.presentation.group.dto.response.GroupChallengeListResponse;
import sleppynavigators.studyupbackend.presentation.group.dto.response.GroupInvitationResponse;
import sleppynavigators.studyupbackend.presentation.group.dto.response.GroupMemberListResponse;
import sleppynavigators.studyupbackend.presentation.group.dto.response.GroupResponse;
import sleppynavigators.studyupbackend.presentation.group.dto.response.GroupTaskListResponse;

@Tag(name = "Group", description = "그룹 관련 API")
@RestController
@RequestMapping("/groups")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupController {

    private final GroupService groupService;
    private final ChatMessageService chatMessageService;
    private final ChallengeService challengeService;

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

    @GetMapping("/{groupId}")
    @Operation(summary = "그룹 조회", description = "그룹을 조회합니다.")
    public ResponseEntity<SuccessResponse<GroupResponse>> getGroup(
            @PathVariable Long groupId
    ) {
        GroupResponse response = groupService.getGroup(groupId);
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
    public ResponseEntity<SuccessResponse<GroupInvitationResponse>> getInvitation(
            @PathVariable Long groupId, @PathVariable Long invitationId
    ) {
        GroupInvitationResponse response = groupService.getInvitation(groupId, invitationId);
        return ResponseEntity.ok(new SuccessResponse<>(response));
    }

    @PostMapping("/{groupId}/invitations/{invitationId}/accept")
    @Operation(summary = "그룹 초대 수락", description = "그룹 초대를 수락합니다.")
    public ResponseEntity<SuccessResponse<GroupInvitationResponse>> acceptInvitation(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long groupId, @PathVariable Long invitationId,
            @RequestBody @Valid GroupInvitationAcceptRequest groupInvitationAcceptRequest) {
        Long userId = userPrincipal.userId();
        GroupInvitationResponse response =
                groupService.acceptInvitation(userId, groupId, invitationId, groupInvitationAcceptRequest);
        return ResponseEntity.ok(new SuccessResponse<>(response));
    }

    @PostMapping("/{groupId}/challenges")
    @Operation(summary = "챌린지 생성", description = "챌린지를 생성합니다.")
    public ResponseEntity<SuccessResponse<ChallengeResponse>> createChallenge(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long groupId,
            @RequestBody @Valid ChallengeCreationRequest challengeCreationRequest
    ) {
        Long userId = userPrincipal.userId();
        ChallengeResponse response = challengeService.createChallenge(userId, groupId, challengeCreationRequest);
        return ResponseEntity.ok(new SuccessResponse<>(response));
    }

    @GetMapping("/{groupId}/challenges")
    @Operation(summary = "그룹 챌린지 목록 조회", description = "그룹의 챌린지 목록을 조회합니다.")
    public ResponseEntity<SuccessResponse<GroupChallengeListResponse>> getChallenges(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long groupId, @SearchParam @Valid ChallengeSearch challengeSearch
    ) {
        Long userId = userPrincipal.userId();
        GroupChallengeListResponse response = groupService.getChallenges(userId, groupId, challengeSearch);
        return ResponseEntity.ok(new SuccessResponse<>(response));
    }

    @GetMapping("/{groupId}/tasks")
    @Operation(summary = "그룹 테스크 목록 조회", description = "그룹의 테스크 목록을 조회합니다.")
    public ResponseEntity<SuccessResponse<GroupTaskListResponse>> getTasks(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long groupId, @SearchParam @Valid TaskSearch taskSearch) {
        Long userId = userPrincipal.userId();
        GroupTaskListResponse response = groupService.getTasks(userId, groupId, taskSearch);
        return ResponseEntity.ok(new SuccessResponse<>(response));
    }

    @GetMapping("/{groupId}/messages")
    @Operation(summary = "그룹 채팅 메시지 조회", description = "그룹의 채팅 메시지를 페이지네이션하여 조회합니다.")
    public ResponseEntity<SuccessResponse<ChatMessageListResponse>> getMessages(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long groupId,
            @SearchParam @Valid ChatMessageSearch chatMessageSearch
    ) {
        Long userId = userPrincipal.userId();
        ChatMessageListResponse response = chatMessageService
                .getMessages(userId, groupId, chatMessageSearch);
        return ResponseEntity.ok(new SuccessResponse<>(response));
    }

    @GetMapping("/{groupId}/members")
    @Operation(summary = "그룹 멤버 조회", description = "그룹의 멤버 목록을 조회합니다.")
    public ResponseEntity<SuccessResponse<GroupMemberListResponse>> getGroupMembers(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long groupId,
            @SearchParam @Valid GroupMemberSearch groupMemberSearch
    ) {
        Long userId = userPrincipal.userId();
        GroupMemberListResponse response = groupService.getGroupMembers(userId, groupId, groupMemberSearch);
        return ResponseEntity.ok(new SuccessResponse<>(response));
    }
}
