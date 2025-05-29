package sleppynavigators.studyupbackend.infrastructure.challenge;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import sleppynavigators.studyupbackend.domain.challenge.Challenge;

public interface ChallengeRepository extends JpaRepository<Challenge, Long>, ChallengeQueryRepository {

    /**
     * <b>Caution</b> <code>Challenge</code>s queried through this method must be registered in the JPA persistence
     * context, so <code>Challenge</code>s retrieved without a lock must <b>not</b> be registered in the JPA persistence
     * context beforehand. Be especially careful with auto-enrollment via JPA direct association.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM challenges c WHERE c.id = :id")
    Optional<Challenge> findByIdForUpdate(Long id);
}
