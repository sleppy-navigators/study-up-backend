package sleppynavigators.studyupbackend.common.support;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sleppynavigators.studyupbackend.domain.challenge.Challenge;
import sleppynavigators.studyupbackend.domain.group.Group;
import sleppynavigators.studyupbackend.domain.user.User;
import sleppynavigators.studyupbackend.infrastructure.challenge.ChallengeRepository;

@Transactional
@Component
public class ChallengeSupport {

    @Autowired
    private ChallengeRepository challengeRepository;

    /**
     * <b>Caution!</b> This method does not update the group given as an argument.
     *
     * @param group        The group to which the challenge belongs.
     * @param taskProgress An array of two integers, where the first element is the number of tasks to be created and
     *                     the second element is the number of tasks to be certified. The number of tasks to be created
     *                     must be greater than or equal to the number of certified tasks.
     * @param creator      The user who creates the challenge.
     */
    public Challenge registerChallengeWithTasks(Group group, int[] taskProgress, User creator) {
        Challenge challenge = Challenge.builder()
                .title("test-challenge")
                .deadline(LocalDateTime.now().plusDays(3))
                .group(group)
                .owner(creator)
                .build();

        int numOfTasks = taskProgress[0];
        int numOfCertified = taskProgress[1];
        for (int ti = 0; ti < numOfTasks; ti++) {
            challenge.addTask("test-task-" + ti, LocalDateTime.now().plusHours(3));

            if (ti < numOfCertified) {
                try {
                    challenge.getTasks().get(ti)
                            .certify(List.of(), List.of(new URL("https://test.com")), creator);
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        challengeRepository.save(challenge);
        return challenge;
    }
}
