package sleppynavigators.studyupbackend.infrastructure.authentication;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import sleppynavigators.studyupbackend.domain.authentication.UserCredential;

public interface UserCredentialRepository extends JpaRepository<UserCredential, Long> {
    Optional<UserCredential> findBySubject(String subject);
}
