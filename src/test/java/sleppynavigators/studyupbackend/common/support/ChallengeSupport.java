package sleppynavigators.studyupbackend.common.support;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sleppynavigators.studyupbackend.application.challenge.ChallengeService;
import sleppynavigators.studyupbackend.application.challenge.TaskCertificationStatus;
import sleppynavigators.studyupbackend.domain.challenge.Challenge;
import sleppynavigators.studyupbackend.domain.challenge.Task;
import sleppynavigators.studyupbackend.domain.group.Group;
import sleppynavigators.studyupbackend.domain.user.User;
import sleppynavigators.studyupbackend.infrastructure.challenge.ChallengeRepository;
import sleppynavigators.studyupbackend.presentation.challenge.dto.request.ChallengeCreationRequest;
import sleppynavigators.studyupbackend.presentation.challenge.dto.request.ChallengeCreationRequest.TaskRequest;
import sleppynavigators.studyupbackend.presentation.challenge.dto.request.TaskCertificationRequest;
import sleppynavigators.studyupbackend.presentation.challenge.dto.request.TaskSearch;
import sleppynavigators.studyupbackend.presentation.challenge.dto.response.ChallengeResponse;
import sleppynavigators.studyupbackend.presentation.challenge.dto.response.TaskListResponse.TaskListItem;

@Transactional
@Component
public class ChallengeSupport {

    @Autowired
    private ChallengeRepository challengeRepository;
    @Autowired
    private ChallengeService challengeService;

    /**
     * <b>Caution!</b> This method does not update the group given as an argument.
     */
    public Challenge callToMakeChallengesWithTasks(
            Group group, Integer numOfTotalTasks, Integer numOfCertifiedTasks, User challenger) {

        // Create a challenge with tasks
        ChallengeCreationRequest challengeCreationRequest = new ChallengeCreationRequest(
                "test-challenge", "test-challenge-description",
                IntStream.range(0, numOfTotalTasks)
                        .mapToObj(i -> new TaskRequest("test-task-" + i, ZonedDateTime.now().plusHours(3)))
                        .toList(),
                10L);
        ChallengeResponse challengeResponse = challengeService
                .createChallenge(challenger.getId(), group.getId(), challengeCreationRequest);

        // Complete the tasks
        try {
            TaskSearch taskSearch = new TaskSearch(0L, 20, TaskCertificationStatus.ALL);
            List<TaskListItem> tasks = challengeService
                    .getTasks(challenger.getId(), challengeResponse.id(), taskSearch)
                    .tasks();
            for (int ti = 0; ti < numOfCertifiedTasks; ti++) {
                challengeService.completeTask(
                        challenger.getId(), challengeResponse.id(), tasks.get(ti).id(),
                        new TaskCertificationRequest(List.of(new URL("https://blog.com/article1")), List.of()));
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        return challengeRepository.findById(challengeResponse.id()).orElseThrow();
    }

    /**
     * <b>Caution!</b> This method does not update the group given as an argument.
     */
    public Challenge callToMakeChallengeWithFailedTasks(Group group, Integer numOfFailedTasks, User challenger) {
        // Create a challenge with tasks
        ChallengeCreationRequest challengeCreationRequest = new ChallengeCreationRequest(
                "test-challenge", "test-challenge-description",
                IntStream.range(0, numOfFailedTasks)
                        .mapToObj(i -> new TaskRequest("test-task-" + i, ZonedDateTime.now().plusSeconds(1)))
                        .toList(),
                10L);
        ChallengeResponse challengeResponse = challengeService
                .createChallenge(challenger.getId(), group.getId(), challengeCreationRequest);

        // Wait for the tasks to be failed
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return challengeRepository.findById(challengeResponse.id()).orElseThrow();
    }

    /**
     * <b>Caution!</b> This method does not update the group given as an argument.
     */
    public Challenge callToMakeCompletedChallengeWithTasks(
            Group group, Integer numOfTotalTasks, User challenger) {

        // Create a challenge with tasks
        ChallengeCreationRequest challengeCreationRequest = new ChallengeCreationRequest(
                "test-challenge", "test-challenge-description",
                IntStream.range(0, numOfTotalTasks)
                        .mapToObj(i -> new TaskRequest("test-task-" + i, ZonedDateTime.now().plusSeconds(2)))
                        .toList(),
                10L);
        ChallengeResponse challengeResponse = challengeService
                .createChallenge(challenger.getId(), group.getId(), challengeCreationRequest);

        // Complete the tasks
        try {
            TaskSearch taskSearch = new TaskSearch(0L, 20, TaskCertificationStatus.ALL);
            List<TaskListItem> tasks = challengeService
                    .getTasks(challenger.getId(), challengeResponse.id(), taskSearch)
                    .tasks();
            for (int ti = 0; ti < numOfTotalTasks; ti++) {
                challengeService.completeTask(
                        challenger.getId(), challengeResponse.id(), tasks.get(ti).id(),
                        new TaskCertificationRequest(List.of(new URL("https://blog.com/article1")), List.of()));
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return challengeRepository.findById(challengeResponse.id()).orElseThrow();
    }

    public void callToHuntTask(User hunter, Challenge challenge, Task target) {
        challengeService.huntTask(hunter.getId(), challenge.getId(), target.getId());
    }

    public void callToCancelChallenge(User user, Challenge challenge) {
        challengeService.cancelChallenge(user.getId(), challenge.getId());
    }

    public void callToCertifyTask(
            User user, Challenge challenge, Long taskId, TaskCertificationRequest taskCertificationRequest) {
        challengeService.completeTask(user.getId(), challenge.getId(), taskId, taskCertificationRequest);
    }
}
