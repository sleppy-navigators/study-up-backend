package sleppynavigators.studyupbackend.infrastructure.authentication;

import jakarta.validation.constraints.NotBlank;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import sleppynavigators.studyupbackend.domain.authentication.UserCredential;

public interface UserCredentialRepository extends JpaRepository<UserCredential, Long> {
    Optional<UserCredential> findBySubject(@NotBlank String subject);
}
