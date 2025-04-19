package sleppynavigators.studyupbackend.infrastructure.challenge;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import sleppynavigators.studyupbackend.domain.challenge.Task;

public interface TaskRepository extends JpaRepository<Task, Long>, TaskQueryRepository {

    Optional<Task> findByIdAndChallengeId(Long id, Long challengeId);
}
