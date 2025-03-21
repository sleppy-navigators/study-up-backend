package sleppynavigators.studyupbackend.application.challenge;

import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sleppynavigators.studyupbackend.domain.challenge.Challenge;
import sleppynavigators.studyupbackend.domain.challenge.Task;
import sleppynavigators.studyupbackend.domain.group.Group;
import sleppynavigators.studyupbackend.domain.user.User;
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

    @Transactional
    public ChallengeResponse createChallenge(Long userId, Long groupId, ChallengeCreationRequest request) {
        User user = userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);
        Group group = groupRepository.findById(groupId).orElseThrow(EntityNotFoundException::new);

        if (!group.hasMember(user)) {
            throw new ForbiddenContentException();
        }

        Challenge challenge = challengeRepository.save(request.toEntity(user, group));
        return ChallengeResponse.fromEntity(challenge);
    }

    public TaskListResponse getTasks(Long userId, Long challengeId) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(EntityNotFoundException::new);
        User user = userRepository.findById(userId)
                .orElseThrow(EntityNotFoundException::new);

        // TODO: filter by certification status utilizing `RSQL` or `QueryDSL Web Support`
        List<Task> tasks = challenge.getTasksForUser(user);
        return TaskListResponse.fromEntities(tasks);
    }

    @Transactional
    public TaskResponse completeTask(Long userId, Long challengeId, Long taskId, TaskCertificationRequest request) {
        Task task = taskRepository.findByIdAndChallengeId(taskId, challengeId)
                .orElseThrow(EntityNotFoundException::new);
        User user = userRepository.findById(userId)
                .orElseThrow(EntityNotFoundException::new);

        try {
            task.certify(request.externalLinks(), request.imageUrls(), user);
            return TaskResponse.fromEntity(task);
        } catch (IllegalArgumentException ignored) {
            throw new InvalidPayloadException();
        }
    }
}
