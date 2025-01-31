package sleppynavigators.studyupbackend.infrastructure.authentication.session;

import org.springframework.data.jpa.repository.JpaRepository;
import sleppynavigators.studyupbackend.domain.authentication.session.UserSession;

public interface UserSessionRepository extends JpaRepository<UserSession, Long> {
}
