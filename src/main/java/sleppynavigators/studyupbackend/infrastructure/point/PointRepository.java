package sleppynavigators.studyupbackend.infrastructure.point;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import sleppynavigators.studyupbackend.domain.point.Point;

public interface PointRepository extends JpaRepository<Point, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM points p WHERE p.user.id = :userId")
    Optional<Point> findByUserIdForUpdate(Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM points p WHERE p.challenge.id = :id")
    Optional<Point> findByChallengeIdForUpdate(Long id);
}
