package sleppynavigators.studyupbackend.infrastructure.challenge;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import sleppynavigators.studyupbackend.domain.challenge.Challenge;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {

    // TODO: sort by `Event`(challenge creation and task certification) utilizing `@SortDefault`
    List<Challenge> findAllByGroupId(Long groupId);
}
