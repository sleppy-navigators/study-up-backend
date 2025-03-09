package sleppynavigators.studyupbackend.infrastructure.challenge;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sleppynavigators.studyupbackend.domain.challenge.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {

    // TODO: filter by deadline utilizing `RSQL` or `QueryDSL Web Support`
    @Query("""
            SELECT t FROM tasks t
            WHERE t.challenge.owner.id = :userId
            """)
    List<Task> findAllByUserId(Long userId);

    // TODO: filter by deadline utilizing `RSQL` or `QueryDSL Web Support`
    // TODO: filter by certification status utilizing `RSQL` or `QueryDSL Web Support`
    @Query("""
            SELECT t FROM tasks t
            WHERE t.challenge.group.id = :groupId
            """)
    List<Task> findAllByGroupId(Long groupId);

    Optional<Task> findByIdAndChallengeId(Long id, Long challengeId);
}
