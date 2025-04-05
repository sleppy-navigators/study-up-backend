package sleppynavigators.studyupbackend.application.challenge;

import java.util.List;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sleppynavigators.studyupbackend.domain.challenge.Challenge;
import sleppynavigators.studyupbackend.domain.challenge.Task;
import sleppynavigators.studyupbackend.domain.event.ChallengeCancelEvent;
import sleppynavigators.studyupbackend.domain.group.Group;
import sleppynavigators.studyupbackend.domain.user.User;
import sleppynavigators.studyupbackend.domain.event.SystemEvent;
import sleppynavigators.studyupbackend.domain.event.ChallengeCreateEvent;
import sleppynavigators.studyupbackend.domain.event.ChallengeCompleteEvent;
import sleppynavigators.studyupbackend.application.event.SystemEventPublisher;
import sleppynavigators.studyupbackend.exception.business.ForbiddenContentException;
import sleppynavigators.studyupbackend.exception.business.InvalidPayloadException;
import sleppynavigators.studyupbackend.exception.database.EntityNotFoundException;
import sleppynavigators.studyupbackend.infrastructure.challenge.ChallengeRepository;
import sleppynavigators.studyupbackend.infrastructure.challenge.TaskRepository;
import sleppynavigators.studyupbackend.infrastructure.group.GroupRepository;
import sleppynavigators.studyupbackend.infrastructure.user.UserRepository;
import sleppynavigators.studyupbackend.presentation.challenge.dto.request.ChallengeCreationRequest;
import sleppynavigators.studyupbackend.presentation.challenge.dto.request.TaskCertificationRequest;
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
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found - userId: " + userId));
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Group not found - groupId: " + groupId));

        if (!group.hasMember(user)) {
            throw new ForbiddenContentException(
                    "User cannot create challenge in this group - userId: " + userId + ", groupId: " + groupId);
        }

        Challenge challenge = challengeRepository.save(request.toEntity(user, group));

        SystemEvent event = new ChallengeCreateEvent(
                user.getUserProfile().username(),
                challenge.getDetail().title(),
                groupId);
        systemEventPublisher.publish(event);

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

        SystemEvent event = new ChallengeCancelEvent(
                user.getUserProfile().username(),
                challenge.getDetail().title(),
                challenge.getGroup().getId());
        systemEventPublisher.publish(event);

        challengeRepository.deleteById(challengeId);
    }

    public TaskListResponse getTasks(Long userId, Long challengeId) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new EntityNotFoundException("Challenge not found - challengeId: " + challengeId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found - userId: " + userId));

        // TODO: filter by certification status utilizing `RSQL` or `QueryDSL Web Support`
        List<Task> tasks = challenge.getTasksForUser(user);
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

            if (task.getChallenge().isAllTasksCompleted()) {
                SystemEvent event = new ChallengeCompleteEvent(
                        user.getUserProfile().username(),
                        task.getChallenge().getDetail().title(),
                        task.getChallenge().getGroup().getId()
                );
                systemEventPublisher.publish(event);
            }

            return TaskResponse.fromEntity(task);
        } catch (IllegalArgumentException ex) {
            throw new InvalidPayloadException(ex);
        }
    }
}
