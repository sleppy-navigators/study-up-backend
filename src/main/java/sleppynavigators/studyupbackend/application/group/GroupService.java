package sleppynavigators.studyupbackend.application.group;

import java.util.List;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sleppynavigators.studyupbackend.domain.challenge.Challenge;
import sleppynavigators.studyupbackend.domain.challenge.Task;
import sleppynavigators.studyupbackend.domain.event.GroupCreateEvent;
import sleppynavigators.studyupbackend.domain.group.Group;
import sleppynavigators.studyupbackend.domain.group.GroupMember;
import sleppynavigators.studyupbackend.domain.group.invitation.GroupInvitation;
import sleppynavigators.studyupbackend.domain.user.User;
import sleppynavigators.studyupbackend.domain.bot.Bot;
import sleppynavigators.studyupbackend.domain.event.SystemEvent;
import sleppynavigators.studyupbackend.domain.event.UserJoinEvent;
import sleppynavigators.studyupbackend.domain.event.UserLeaveEvent;
import sleppynavigators.studyupbackend.application.event.SystemEventPublisher;
import sleppynavigators.studyupbackend.exception.business.ForbiddenContentException;
import sleppynavigators.studyupbackend.exception.business.InvalidPayloadException;
import sleppynavigators.studyupbackend.exception.database.EntityNotFoundException;
import sleppynavigators.studyupbackend.infrastructure.challenge.ChallengeRepository;
import sleppynavigators.studyupbackend.infrastructure.challenge.TaskRepository;
import sleppynavigators.studyupbackend.infrastructure.group.GroupMemberRepository;
import sleppynavigators.studyupbackend.infrastructure.group.GroupRepository;
import sleppynavigators.studyupbackend.infrastructure.group.invitation.GroupInvitationRepository;
import sleppynavigators.studyupbackend.infrastructure.user.UserRepository;
import sleppynavigators.studyupbackend.infrastructure.bot.BotRepository;
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
    private final BotRepository botRepository;
    private final SystemEventPublisher systemEventPublisher;

    public GroupResponse getGroup(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Group not found - groupId: " + groupId));
        return GroupResponse.fromEntity(group);
    }

    @Transactional
    public GroupResponse createGroup(Long creatorId, GroupCreationRequest request) {
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new EntityNotFoundException("User not found - userId: " + creatorId));
        Group savedGroup = groupRepository.save(request.toEntity(creator));

        Bot bot = new Bot(savedGroup);
        botRepository.save(bot);

        SystemEvent event = new GroupCreateEvent(
                creator.getUserProfile().username(),
                savedGroup.getGroupDetail().name(),
                savedGroup.getId());
        systemEventPublisher.publish(event);

        return GroupResponse.fromEntity(savedGroup);
    }

    @Transactional
    public void leaveGroup(Long userId, Long groupId) {
        groupMemberRepository.findByGroupIdAndUserId(groupId, userId).ifPresent(member -> {
            Group group = member.getGroup();
            User user = member.getUser();
            group.removeMember(member);

            if (!group.hasAnyMember()) {
                botRepository.findByGroupId(groupId).ifPresent(botRepository::delete);
                groupRepository.delete(group);
            } else {
                SystemEvent event = new UserLeaveEvent(user.getUserProfile().username(), groupId);
                systemEventPublisher.publish(event);
            }
        });
    }

    public GroupInvitationResponse getInvitation(Long groupId, Long invitationId) {
        GroupInvitation invitation = groupInvitationRepository.findById(invitationId)
                .orElseThrow(() -> new EntityNotFoundException("Invitation not found - invitationId: " + invitationId));

        if (!invitation.matchGroupId(groupId)) {
            throw new InvalidPayloadException("Invalid groupId - groupId: " + groupId);
        }

        return GroupInvitationResponse.fromEntity(invitation);
    }

    @Transactional
    public GroupInvitationResponse makeInvitation(Long groupId, Long inviterId) {
        GroupMember inviter = groupMemberRepository.findByGroupIdAndUserId(groupId, inviterId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Group member not found - groupId: " + groupId + ", userId: " + inviterId));
        GroupInvitation invitation = groupInvitationRepository.save(new GroupInvitation(inviter.getGroup()));
        return GroupInvitationResponse.fromEntity(invitation);
    }

    @Transactional
    public GroupInvitationResponse acceptInvitation(
            Long userId, Long groupId, Long invitationId, GroupInvitationAcceptRequest request) {
        GroupInvitation invitation = groupInvitationRepository.findById(invitationId)
                .orElseThrow(() -> new EntityNotFoundException("Invitation not found - invitationId: " + invitationId));

        if (!invitation.matchGroupId(groupId) || !invitation.matchKey(request.invitationKey())) {
            throw new InvalidPayloadException(
                    "Invalid groupId or invitationKey - groupId: " + groupId +
                            ", invitationKey: " + request.invitationKey());
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found - userId: " + userId));
        invitation.getGroup().addMember(user);

        SystemEvent event = new UserJoinEvent(user.getUserProfile().username(), groupId);
        systemEventPublisher.publish(event);

        return GroupInvitationResponse.fromEntity(invitation);
    }

    public GroupChallengeListResponse getChallenges(Long userId, Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Group not found - groupId: " + groupId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found - userId: " + userId));

        if (!group.hasMember(user)) {
            throw new ForbiddenContentException(
                    "User cannot access this group - userId: " + userId + ", groupId: " + groupId);
        }

        List<Challenge> challenges = challengeRepository.findAllByGroupId(groupId);
        return GroupChallengeListResponse.fromEntities(challenges);
    }

    public GroupTaskListResponse getTasks(Long userId, Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Group not found - groupId: " + groupId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found - userId: " + userId));

        if (!group.hasMember(user)) {
            throw new ForbiddenContentException(
                    "User cannot access this group - userId: " + userId + ", groupId: " + groupId);
        }

        List<Task> tasks = taskRepository.findAllByChallengeGroupId(groupId);
        return GroupTaskListResponse.fromEntities(tasks);
    }
}
