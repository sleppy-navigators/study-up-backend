package sleppynavigators.studyupbackend.infrastructure.challenge;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import sleppynavigators.studyupbackend.domain.challenge.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {

    // TODO: filter by deadline utilizing `RSQL` or `QueryDSL Web Support`
    List<Task> findAllByChallengeOwnerId(Long userId);

    // TODO: filter by deadline utilizing `RSQL` or `QueryDSL Web Support`
    // TODO: filter by certification status utilizing `RSQL` or `QueryDSL Web Support`
    List<Task> findAllByChallengeGroupId(Long groupId);

    Optional<Task> findByIdAndChallengeId(Long id, Long challengeId);
}
