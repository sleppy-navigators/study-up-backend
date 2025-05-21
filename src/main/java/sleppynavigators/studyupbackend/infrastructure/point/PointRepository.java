package sleppynavigators.studyupbackend.infrastructure.point;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import sleppynavigators.studyupbackend.domain.point.Point;

public interface PointRepository extends JpaRepository<Point, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT u.equity FROM users u WHERE u.id = :userId")
    Optional<Point> findByUserIdForUpdate(Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c.deposit FROM challenges c WHERE c.id = :id")
    Optional<Point> findByChallengeIdForUpdate(Long id);
}
