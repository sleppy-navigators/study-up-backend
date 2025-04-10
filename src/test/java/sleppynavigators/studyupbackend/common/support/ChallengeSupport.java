package sleppynavigators.studyupbackend.common.support;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sleppynavigators.studyupbackend.application.challenge.ChallengeService;
import sleppynavigators.studyupbackend.domain.challenge.Challenge;
import sleppynavigators.studyupbackend.domain.group.Group;
import sleppynavigators.studyupbackend.domain.user.User;
import sleppynavigators.studyupbackend.infrastructure.challenge.ChallengeRepository;
import sleppynavigators.studyupbackend.presentation.challenge.dto.request.ChallengeCreationRequest;
import sleppynavigators.studyupbackend.presentation.challenge.dto.request.ChallengeCreationRequest.TaskRequest;
import sleppynavigators.studyupbackend.presentation.challenge.dto.request.TaskCertificationRequest;
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
                "test-challenge",
                ZonedDateTime.now().plusDays(3),
                "test-challenge-description",
                IntStream.range(0, numOfTotalTasks)
                        .mapToObj(i -> new TaskRequest("test-task-" + i, ZonedDateTime.now().plusHours(3)))
                        .toList());
        ChallengeResponse challengeResponse = challengeService
                .createChallenge(challenger.getId(), group.getId(), challengeCreationRequest);

        // Complete the tasks
        try {
            List<TaskListItem> tasks = challengeService.getTasks(challenger.getId(), challengeResponse.id()).tasks();
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

    public void callToCancelChallenge(User user, Challenge challenge) {
        challengeService.cancelChallenge(user.getId(), challenge.getId());
    }
}
