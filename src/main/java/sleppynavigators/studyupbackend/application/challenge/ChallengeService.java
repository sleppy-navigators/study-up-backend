package sleppynavigators.studyupbackend.application.challenge;

import com.querydsl.core.types.Predicate;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sleppynavigators.studyupbackend.application.event.SystemEventPublisher;
import sleppynavigators.studyupbackend.domain.challenge.Challenge;
import sleppynavigators.studyupbackend.domain.challenge.Task;
import sleppynavigators.studyupbackend.domain.event.ChallengeCancelEvent;
import sleppynavigators.studyupbackend.domain.event.ChallengeCreateEvent;
import sleppynavigators.studyupbackend.domain.event.SystemEvent;
import sleppynavigators.studyupbackend.domain.event.TaskCertifyEvent;
import sleppynavigators.studyupbackend.domain.group.Group;
import sleppynavigators.studyupbackend.domain.user.User;
import sleppynavigators.studyupbackend.exception.business.ForbiddenContentException;
import sleppynavigators.studyupbackend.exception.business.InvalidPayloadException;
import sleppynavigators.studyupbackend.exception.database.EntityNotFoundException;
import sleppynavigators.studyupbackend.exception.database.LockFailedException;
import sleppynavigators.studyupbackend.infrastructure.challenge.ChallengeRepository;
import sleppynavigators.studyupbackend.infrastructure.challenge.TaskQueryOptions;
import sleppynavigators.studyupbackend.infrastructure.challenge.TaskRepository;
import sleppynavigators.studyupbackend.infrastructure.group.GroupRepository;
import sleppynavigators.studyupbackend.infrastructure.user.UserRepository;
import sleppynavigators.studyupbackend.presentation.challenge.dto.request.ChallengeCreationRequest;
import sleppynavigators.studyupbackend.presentation.challenge.dto.request.TaskCertificationRequest;
import sleppynavigators.studyupbackend.presentation.challenge.dto.request.TaskSearch;
import sleppynavigators.studyupbackend.presentation.challenge.dto.response.ChallengeResponse;
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
    private final SystemEventPublisher systemEventPublisher;

    @Transactional
    public ChallengeResponse createChallenge(Long userId, Long groupId, ChallengeCreationRequest request) {
        try {
            if (userRepository.lockById(userId) != 1) {
                throw new LockFailedException("Failed to lock user - userId: " + userId);
            }

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found - userId: " + userId));
            Group group = groupRepository.findById(groupId)
                    .orElseThrow(() -> new EntityNotFoundException("Group not found - groupId: " + groupId));

            if (!group.hasMember(user)) {
                throw new ForbiddenContentException(
                        "User cannot create challenge in this group - userId: " + userId + ", groupId: " + groupId);
            }

            user.deductEquity(request.deposit());
            Challenge challenge = challengeRepository.save(request.toEntity(user, group));

            SystemEvent event = new ChallengeCreateEvent(
                    user.getUserProfile().getUsername(),
                    challenge.getDetail().getTitle(),
                    groupId);
            systemEventPublisher.publish(event);

            return ChallengeResponse.fromEntity(challenge);
        } finally {
            userRepository.unlockById(userId);
        }
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

        SystemEvent event = new ChallengeCancelEvent(
                user.getUserProfile().getUsername(),
                challenge.getDetail().getTitle(),
                challenge.getGroup().getId());
        systemEventPublisher.publish(event);

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

            SystemEvent event = new TaskCertifyEvent(
                    user.getUserProfile().getUsername(),
                    task.getDetail().getTitle(),
                    task.getChallenge().getDetail().getTitle(),
                    task.getChallenge().getGroup().getId()
            );
            systemEventPublisher.publish(event);

            return TaskResponse.fromEntity(task);
        } catch (IllegalArgumentException ex) {
            throw new InvalidPayloadException(ex);
        }
    }

    @Transactional
    public void settlementReward(Long challengerId, Long challengeId) {
        try {
            if (userRepository.lockById(challengerId) != 1) {
                throw new LockFailedException("Failed to lock user - userId: " + challengerId);
            }

            Challenge challenge = challengeRepository.findById(challengeId)
                    .orElseThrow(
                            () -> new EntityNotFoundException("Challenge not found - challengeId: " + challengeId));
            challenge.rewardToOwner();
        } finally {
            userRepository.unlockById(challengerId);
        }
    }
}
