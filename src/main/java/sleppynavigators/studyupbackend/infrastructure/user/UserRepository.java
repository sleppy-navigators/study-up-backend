package sleppynavigators.studyupbackend.infrastructure.user;

import org.springframework.data.jpa.repository.JpaRepository;
import sleppynavigators.studyupbackend.domain.user.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
