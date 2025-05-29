package sleppynavigators.studyupbackend.application.challenge;

import com.querydsl.core.types.Predicate;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sleppynavigators.studyupbackend.application.event.SystemMessageEventPublisher;
import sleppynavigators.studyupbackend.domain.challenge.Challenge;
import sleppynavigators.studyupbackend.domain.challenge.Task;
import sleppynavigators.studyupbackend.domain.challenge.hunting.Hunting;
import sleppynavigators.studyupbackend.domain.event.ChallengeCancelEvent;
import sleppynavigators.studyupbackend.domain.event.ChallengeCreateEvent;
import sleppynavigators.studyupbackend.domain.event.TaskCertifyEvent;
import sleppynavigators.studyupbackend.domain.group.Group;
import sleppynavigators.studyupbackend.domain.user.User;
import sleppynavigators.studyupbackend.exception.business.ForbiddenContentException;
import sleppynavigators.studyupbackend.exception.business.InvalidPayloadException;
import sleppynavigators.studyupbackend.exception.database.EntityNotFoundException;
import sleppynavigators.studyupbackend.infrastructure.challenge.ChallengeRepository;
import sleppynavigators.studyupbackend.infrastructure.challenge.TaskQueryOptions;
import sleppynavigators.studyupbackend.infrastructure.challenge.TaskRepository;
import sleppynavigators.studyupbackend.infrastructure.challenge.hunting.HuntingRepository;
import sleppynavigators.studyupbackend.infrastructure.group.GroupRepository;
import sleppynavigators.studyupbackend.infrastructure.user.UserRepository;
import sleppynavigators.studyupbackend.presentation.challenge.dto.request.ChallengeCreationRequest;
import sleppynavigators.studyupbackend.presentation.challenge.dto.request.TaskCertificationRequest;
import sleppynavigators.studyupbackend.presentation.challenge.dto.request.TaskSearch;
import sleppynavigators.studyupbackend.presentation.challenge.dto.response.ChallengeResponse;
import sleppynavigators.studyupbackend.presentation.challenge.dto.response.HuntingResponse;
import sleppynavigators.studyupbackend.presentation.challenge.dto.response.TaskListResponse;
import sleppynavigators.studyupbackend.presentation.challenge.dto.response.TaskResponse;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final HuntingRepository huntingRepository;
    private final SystemMessageEventPublisher systemMessageEventPublisher;

    @Transactional
    public ChallengeResponse createChallenge(Long userId, Long groupId, ChallengeCreationRequest request) {
        User user = userRepository.findByIdForUpdate(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found - userId: " + userId));
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Group not found - groupId: " + groupId));

        if (!group.hasMember(user)) {
            throw new ForbiddenContentException(
                    "User cannot create challenge in this group - userId: " + userId + ", groupId: " + groupId);
        }

        user.deductPoint(request.deposit());
        Challenge challenge = challengeRepository.save(request.toEntity(user, group));

        ChallengeCreateEvent event = new ChallengeCreateEvent(
                user.getUserProfile().getUsername(),
                challenge.getDetail().getTitle(),
                groupId);
        systemMessageEventPublisher.publish(event);

        return ChallengeResponse.fromEntity(challenge);
    }

    @Transactional
    public void cancelChallenge(Long userId, Long challengeId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found - userId: " + userId));
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new EntityNotFoundException("Challenge not found - challengeId: " + challengeId));

        if (!challenge.canModify(user)) {
            throw new ForbiddenContentException(
                    "User cannot modify this challenge - userId: " + userId + ", challengeId: " + challengeId);
        }

        ChallengeCancelEvent event = new ChallengeCancelEvent(
                user.getUserProfile().getUsername(),
                challenge.getDetail().getTitle(),
                challenge.getGroup().getId());
        systemMessageEventPublisher.publish(event);

        challengeRepository.deleteById(challengeId);
    }

    public TaskListResponse getTasks(Long userId, Long challengeId, TaskSearch search) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new EntityNotFoundException("Challenge not found - challengeId: " + challengeId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found - userId: " + userId));

        if (!challenge.canAccess(user)) {
            throw new ForbiddenContentException(
                    "User cannot access this challenge - userId: " + user.getId() + ", challengeId: " + challengeId);
        }

        Predicate predicate = TaskQueryOptions.getChallengePredicate(challengeId)
                .and(TaskQueryOptions.getStatusPredicate(search.status()));
        List<Task> tasks = taskRepository.findAll(predicate, search.pageNum(), search.pageSize());
        return TaskListResponse.fromEntities(tasks);
    }

    @Transactional
    public TaskResponse completeTask(Long userId, Long challengeId, Long taskId, TaskCertificationRequest request) {
        Task task = taskRepository.findByIdAndChallengeId(taskId, challengeId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found - taskId: " + taskId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found - userId: " + userId));

        try {
            task.certify(request.externalLinks(), request.imageUrls(), user);

            TaskCertifyEvent event = new TaskCertifyEvent(
                    user.getUserProfile().getUsername(),
                    task.getDetail().getTitle(),
                    task.getChallenge().getDetail().getTitle(),
                    task.getChallenge().getGroup().getId()
            );
            systemMessageEventPublisher.publish(event);

            return TaskResponse.fromEntity(task);
        } catch (IllegalArgumentException ex) {
            throw new InvalidPayloadException(ex);
        }
    }

    @Transactional
    public HuntingResponse huntTask(Long userId, Long challengeId, Long taskId) {
        User user = userRepository.findByIdForUpdate(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found - userId: " + userId));
        Challenge challenge = challengeRepository.findByIdForUpdate(challengeId)
                .orElseThrow(() -> new EntityNotFoundException("Challenge not found - challengeId: " + challengeId));

        if (!challenge.canHunt(user)) {
            throw new ForbiddenContentException(
                    "User cannot hunt this challenge - userId: " + userId + ", challengeId: " + challengeId);
        }

        // Caution!
        // This includes calculating the cap on the number of hunters per task,
        // which requires you to watch out for Phantom Leads.
        // (We use InnoDB's Repeatable Read isolation level)
        Hunting hunting = challenge.rewardToHunter(taskId, user);
        Hunting saved = huntingRepository.save(hunting);
        return HuntingResponse.fromEntity(saved);
    }

    @Transactional
    public void settlementReward(Long challengerId, Long challengeId) {
        // Lock the user to prevent concurrent modifications
        User challenger = userRepository.findByIdForUpdate(challengerId)
                .orElseThrow(() -> new EntityNotFoundException("User not found - userId: " + challengerId));

        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Challenge not found - challengeId: " + challengeId));
        challenge.rewardToOwner();
    }
}
