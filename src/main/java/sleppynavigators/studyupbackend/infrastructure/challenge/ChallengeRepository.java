package sleppynavigators.studyupbackend.infrastructure.challenge;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sleppynavigators.studyupbackend.domain.challenge.Challenge;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
    
    // TODO: sort by `Event`(challenge creation and task certification) utilizing `@SortDefault`
    @Query("""
            SELECT ch FROM challenges ch
            WHERE ch.group.id = :groupId
            """)
    List<Challenge> findAllByGroupId(Long groupId);
}
