package sleppynavigators.studyupbackend.application.group;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sleppynavigators.studyupbackend.domain.challenge.Challenge;
import sleppynavigators.studyupbackend.domain.challenge.Task;
import sleppynavigators.studyupbackend.domain.chat.Bot;
import sleppynavigators.studyupbackend.domain.event.group.GroupCreateEvent;
import sleppynavigators.studyupbackend.domain.event.group.UserJoinEvent;
import sleppynavigators.studyupbackend.domain.event.group.UserLeaveEvent;
import sleppynavigators.studyupbackend.domain.group.Group;
import sleppynavigators.studyupbackend.domain.group.GroupMember;
import sleppynavigators.studyupbackend.domain.group.invitation.GroupInvitation;
import sleppynavigators.studyupbackend.domain.user.User;
import sleppynavigators.studyupbackend.exception.business.ForbiddenContentException;
import sleppynavigators.studyupbackend.exception.business.InvalidPayloadException;
import sleppynavigators.studyupbackend.exception.database.EntityNotFoundException;
import sleppynavigators.studyupbackend.infrastructure.challenge.ChallengeQueryOptions;
import sleppynavigators.studyupbackend.infrastructure.challenge.ChallengeRepository;
import sleppynavigators.studyupbackend.infrastructure.challenge.TaskQueryOptions;
import sleppynavigators.studyupbackend.infrastructure.challenge.TaskRepository;
import sleppynavigators.studyupbackend.infrastructure.chat.BotRepository;
import sleppynavigators.studyupbackend.infrastructure.group.GroupMemberRepository;
import sleppynavigators.studyupbackend.infrastructure.group.GroupRepository;
import sleppynavigators.studyupbackend.infrastructure.group.invitation.GroupInvitationRepository;
import sleppynavigators.studyupbackend.infrastructure.user.UserRepository;
import sleppynavigators.studyupbackend.presentation.challenge.dto.request.ChallengeSearch;
import sleppynavigators.studyupbackend.presentation.challenge.dto.request.TaskSearch;
import sleppynavigators.studyupbackend.presentation.group.dto.request.GroupCreationRequest;
import sleppynavigators.studyupbackend.presentation.group.dto.request.GroupInvitationAcceptRequest;
import sleppynavigators.studyupbackend.presentation.group.dto.request.GroupMemberSearch;
import sleppynavigators.studyupbackend.presentation.group.dto.response.GroupChallengeListResponse;
import sleppynavigators.studyupbackend.presentation.group.dto.response.GroupInvitationResponse;
import sleppynavigators.studyupbackend.presentation.group.dto.response.GroupMemberListResponse;
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
    private final ApplicationEventPublisher eventPublisher;

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

        GroupCreateEvent event = new GroupCreateEvent(
                creator.getUserProfile().getUsername(),
                savedGroup.getGroupDetail().getName(),
                savedGroup.getId());
        eventPublisher.publishEvent(event);

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
                UserLeaveEvent event = new UserLeaveEvent(user.getUserProfile().getUsername(), groupId);
                eventPublisher.publishEvent(event);
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

        UserJoinEvent event = new UserJoinEvent(user.getUserProfile().getUsername(), groupId, userId);
        eventPublisher.publishEvent(event);

        return GroupInvitationResponse.fromEntity(invitation);
    }

    public GroupChallengeListResponse getChallenges(Long userId, Long groupId, ChallengeSearch search) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Group not found - groupId: " + groupId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found - userId: " + userId));

        if (!group.hasMember(user)) {
            throw new ForbiddenContentException(
                    "User cannot access this group - userId: " + userId + ", groupId: " + groupId);
        }

        BooleanExpression predicate = ChallengeQueryOptions.getGroupPredicate(groupId);
        List<Challenge> challenges = switch (search.sortBy()) {
            case LATEST_CERTIFICATION -> challengeRepository
                    .findAllSortedByCertificationDate(predicate, search.pageNum(), search.pageSize());
            case NONE -> challengeRepository
                    .findAll(predicate, search.pageNum(), search.pageSize());
        };
        return GroupChallengeListResponse.fromEntities(challenges);
    }

    public GroupTaskListResponse getTasks(Long userId, Long groupId, TaskSearch search) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Group not found - groupId: " + groupId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found - userId: " + userId));

        if (!group.hasMember(user)) {
            throw new ForbiddenContentException(
                    "User cannot access this group - userId: " + userId + ", groupId: " + groupId);
        }

        Predicate predicate = TaskQueryOptions.getGroupPredicate(groupId)
                .and(TaskQueryOptions.getStatusPredicate(search.status()));
        List<Task> tasks = taskRepository.findAll(predicate, search.pageNum(), search.pageSize());
        return GroupTaskListResponse.fromEntities(tasks);
    }

    public GroupMemberListResponse getGroupMembers(Long userId, Long groupId, GroupMemberSearch groupMemberSearch) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Group not found - groupId: " + groupId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found - userId: " + userId));

        if (!group.hasMember(user)) {
            throw new ForbiddenContentException(
                    "User cannot access this group - userId: " + userId + ", groupId: " + groupId);
        }

        List<GroupMember> members = groupMemberRepository.findAllByGroupId(group.getId());
        List<GroupMember> sortedMembers = sortGroupMemberListResponse(members, groupMemberSearch.sortBy());
        return GroupMemberListResponse.fromEntities(sortedMembers);
    }

    // TODO: Consider de-normalizing the GroupMember entity and sorting directly in the database query.
    private List<GroupMember> sortGroupMemberListResponse(List<GroupMember> members, GroupMemberSortType sortType) {
        return switch (sortType) {
            case POINT -> members.stream()
                    .sorted((m1, m2) -> Long.compare(m2.getPoints(), m1.getPoints()))
                    .toList();
            case AVERAGE_CHALLENGE_COMPLETION_RATE -> members.stream()
                    .sorted((m1, m2) ->
                            Double.compare(m2.calcAvgChallengeCompletionRate(), m1.calcAvgChallengeCompletionRate()))
                    .toList();
            case HUNTING_COUNT -> members.stream()
                    .sorted((m1, m2) ->
                            Long.compare(m2.calcHuntingCount(), m1.calcHuntingCount()))
                    .toList();
            case NONE -> members; // No sorting, return as is
        };
    }
}
