package sleppynavigators.studyupbackend.infrastructure.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import sleppynavigators.studyupbackend.domain.user.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserProfileEmail(String email);
}
