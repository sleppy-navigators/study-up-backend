package sleppynavigators.studyupbackend.infrastructure.challenge;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import sleppynavigators.studyupbackend.domain.challenge.Challenge;

public interface ChallengeRepository extends JpaRepository<Challenge, Long>, JpaSpecificationExecutor<Challenge> {

    List<Challenge> findAllByDetailDeadlineBefore(LocalDateTime deadline);
}
