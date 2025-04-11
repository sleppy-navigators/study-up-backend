package sleppynavigators.studyupbackend.infrastructure.challenge;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import sleppynavigators.studyupbackend.domain.challenge.Task;

public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {

    Optional<Task> findByIdAndChallengeId(Long id, Long challengeId);
}
