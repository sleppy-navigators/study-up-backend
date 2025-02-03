package sleppynavigators.studyupbackend.infrastructure.authentication.session;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import sleppynavigators.studyupbackend.domain.authentication.session.UserSession;

public interface UserSessionRepository extends JpaRepository<UserSession, Long> {
    Optional<UserSession> findByUserId(Long userId);
}
