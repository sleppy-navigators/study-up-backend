package sleppynavigators.studyupbackend.infrastructure.challenge;

import org.springframework.data.jpa.repository.JpaRepository;
import sleppynavigators.studyupbackend.domain.challenge.Challenge;

public interface ChallengeRepository extends JpaRepository<Challenge, Long>, ChallengeQueryRepository {
}
