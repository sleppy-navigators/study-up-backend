package sleppynavigators.studyupbackend.application.group;

import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sleppynavigators.studyupbackend.domain.challenge.Challenge;
import sleppynavigators.studyupbackend.domain.challenge.Task;
import sleppynavigators.studyupbackend.domain.group.Group;
import sleppynavigators.studyupbackend.domain.group.GroupMember;
import sleppynavigators.studyupbackend.domain.group.invitation.GroupInvitation;
import sleppynavigators.studyupbackend.domain.user.User;
import sleppynavigators.studyupbackend.exception.business.ForbiddenContentException;
import sleppynavigators.studyupbackend.exception.business.InvalidPayloadException;
import sleppynavigators.studyupbackend.exception.database.EntityNotFoundException;
import sleppynavigators.studyupbackend.infrastructure.challenge.ChallengeRepository;
import sleppynavigators.studyupbackend.infrastructure.challenge.TaskRepository;
import sleppynavigators.studyupbackend.infrastructure.group.GroupMemberRepository;
import sleppynavigators.studyupbackend.infrastructure.group.GroupRepository;
import sleppynavigators.studyupbackend.infrastructure.group.invitation.GroupInvitationRepository;
import sleppynavigators.studyupbackend.infrastructure.user.UserRepository;
import sleppynavigators.studyupbackend.presentation.group.dto.request.GroupCreationRequest;
import sleppynavigators.studyupbackend.presentation.group.dto.request.GroupInvitationAcceptRequest;
import sleppynavigators.studyupbackend.presentation.group.dto.response.GroupChallengeListResponse;
import sleppynavigators.studyupbackend.presentation.group.dto.response.GroupInvitationResponse;
import sleppynavigators.studyupbackend.presentation.group.dto.response.GroupResponse;
import sleppynavigators.studyupbackend.presentation.group.dto.response.GroupTaskListResponse;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupService {

    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupInvitationRepository groupInvitationRepository;
    private final ChallengeRepository challengeRepository;
    private final TaskRepository taskRepository;


    @Transactional
    public GroupResponse createGroup(Long creatorId, GroupCreationRequest request) {
        User creator = userRepository.findById(creatorId).orElseThrow(EntityNotFoundException::new);
        Group savedGroup = groupRepository.save(request.toEntity(creator));
        return GroupResponse.fromEntity(savedGroup);
    }

    @Transactional
    public void leaveGroup(Long userId, Long groupId) {
        groupMemberRepository.findByGroupIdAndUserId(groupId, userId).ifPresent(member -> {
            Group group = member.getGroup();
            group.removeMember(member);

            if (!group.hasAnyMember()) {
                groupRepository.delete(group);
            }
        });
    }

    public GroupResponse getInvitedGroup(Long groupId, Long invitationId) {
        GroupInvitation invitation = groupInvitationRepository.findById(invitationId)
                .orElseThrow(EntityNotFoundException::new);

        if (!invitation.matchGroupId(groupId)) {
            throw new InvalidPayloadException();
        }

        return GroupResponse.fromEntity(invitation.getGroup());
    }

    @Transactional
    public GroupInvitationResponse makeInvitation(Long groupId, Long inviterId) {
        GroupMember inviter = groupMemberRepository.findByGroupIdAndUserId(groupId, inviterId)
                .orElseThrow(EntityNotFoundException::new);
        GroupInvitation invitation = groupInvitationRepository.save(new GroupInvitation(inviter.getGroup()));
        return GroupInvitationResponse.fromEntity(invitation);
    }

    @Transactional
    public GroupResponse acceptInvitation(
            Long userId, Long groupId, Long invitationId, GroupInvitationAcceptRequest request) {
        GroupInvitation invitation = groupInvitationRepository.findById(invitationId)
                .orElseThrow(EntityNotFoundException::new);
        Group group = invitation.getGroup();

        if (!invitation.matchGroupId(groupId) || !invitation.matchKey(request.invitationKey())) {
            throw new InvalidPayloadException();
        }

        User user = userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);
        if (groupMemberRepository.findByGroupIdAndUserId(groupId, userId).isPresent()) {
            return GroupResponse.fromEntity(group);
        }

        group.addMember(user);
        return GroupResponse.fromEntity(group);
    }

    public GroupChallengeListResponse getChallenges(Long userId, Long groupId) {
        Group group = groupRepository.findById(groupId).orElseThrow(EntityNotFoundException::new);
        User user = userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);

        if (!group.hasMember(user)) {
            throw new ForbiddenContentException();
        }

        List<Challenge> challenges = challengeRepository.findAllByGroupId(groupId);
        return GroupChallengeListResponse.fromEntities(challenges);
    }

    public GroupTaskListResponse getTasks(Long userId, Long groupId) {
        Group group = groupRepository.findById(groupId).orElseThrow(EntityNotFoundException::new);
        User user = userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);

        if (!group.hasMember(user)) {
            throw new ForbiddenContentException();
        }

        List<Task> tasks = taskRepository.findAllByChallengeGroupId(groupId);
        return GroupTaskListResponse.fromEntities(tasks);
    }
}
