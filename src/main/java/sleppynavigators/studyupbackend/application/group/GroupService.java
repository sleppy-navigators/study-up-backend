package sleppynavigators.studyupbackend.application.group;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sleppynavigators.studyupbackend.domain.group.Group;
import sleppynavigators.studyupbackend.domain.group.GroupMember;
import sleppynavigators.studyupbackend.domain.group.invitation.GroupInvitation;
import sleppynavigators.studyupbackend.domain.user.User;
import sleppynavigators.studyupbackend.exception.business.InvalidPayloadException;
import sleppynavigators.studyupbackend.exception.database.EntityNotFoundException;
import sleppynavigators.studyupbackend.infrastructure.group.GroupMemberRepository;
import sleppynavigators.studyupbackend.infrastructure.group.GroupRepository;
import sleppynavigators.studyupbackend.infrastructure.group.invitation.GroupInvitationRepository;
import sleppynavigators.studyupbackend.infrastructure.user.UserRepository;
import sleppynavigators.studyupbackend.presentation.group.dto.request.GroupCreationRequest;
import sleppynavigators.studyupbackend.presentation.group.dto.request.GroupInvitationAcceptRequest;
import sleppynavigators.studyupbackend.presentation.group.dto.response.GroupInvitationResponse;
import sleppynavigators.studyupbackend.presentation.group.dto.response.GroupListResponse;
import sleppynavigators.studyupbackend.presentation.group.dto.response.GroupListResponse.GroupListItem;
import sleppynavigators.studyupbackend.presentation.group.dto.response.SimpleGroupResponse;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupService {

    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupInvitationRepository groupInvitationRepository;

    public GroupListResponse getGroups(Long userId) {
        return new GroupListResponse(
                groupRepository.findByUserId(userId).stream().map(GroupListItem::fromEntity).toList());
    }

    @Transactional
    public SimpleGroupResponse createGroup(Long creatorId, GroupCreationRequest request) {
        User creator = userRepository.findById(creatorId).orElseThrow(EntityNotFoundException::new);
        Group savedGroup = groupRepository.save(request.toEntity(creator));
        return SimpleGroupResponse.fromEntity(savedGroup);
    }

    @Transactional
    public void leaveGroup(Long userId, Long groupId) {
        Group group = groupRepository.findById(groupId).orElseThrow(EntityNotFoundException::new);
        GroupMember targetMember = groupMemberRepository.findByGroupIdAndUserId(groupId, userId)
                .orElseThrow(EntityNotFoundException::new);

        group.removeMember(targetMember);
        if (!group.hasAnyMember()) {
            groupRepository.delete(group);
        }
    }

    public SimpleGroupResponse getInvitedGroup(Long groupId, Long invitationId) {
        GroupInvitation invitation = groupInvitationRepository.findById(invitationId)
                .orElseThrow(EntityNotFoundException::new);
        Group invitedGroup = invitation.getGroup();

        if (!invitedGroup.getId().equals(groupId)) {
            throw new InvalidPayloadException();
        }

        return SimpleGroupResponse.fromEntity(invitation.getGroup());
    }

    @Transactional
    public GroupInvitationResponse makeInvitation(Long groupId, Long inviterId) {
        GroupMember inviter = groupMemberRepository.findByGroupIdAndUserId(groupId, inviterId)
                .orElseThrow(EntityNotFoundException::new);
        GroupInvitation invitation = groupInvitationRepository.save(new GroupInvitation(inviter.getGroup()));
        return GroupInvitationResponse.fromEntity(invitation);
    }

    @Transactional
    public SimpleGroupResponse acceptInvitation(
            Long userId, Long groupId, Long invitationId, GroupInvitationAcceptRequest request) {
        GroupInvitation invitation = groupInvitationRepository.findById(invitationId)
                .orElseThrow(EntityNotFoundException::new);
        Group group = invitation.getGroup();

        if (!group.getId().equals(groupId) || !invitation.matchKey(request.invitationKey())) {
            throw new InvalidPayloadException();
        }

        User user = userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);
        group.addMember(user);
        return SimpleGroupResponse.fromEntity(group);
    }
}
