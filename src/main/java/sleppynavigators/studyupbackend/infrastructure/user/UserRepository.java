package sleppynavigators.studyupbackend.infrastructure.user;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import sleppynavigators.studyupbackend.domain.user.User;

public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * <b>Caution</b> <code>User</code>s queried through this method must be registered in the JPA persistence context,
     * so <code>User</code>s retrieved without a lock must <b>not</b> be registered in the JPA persistence context
     * beforehand. Be especially careful with auto-enrollment via JPA direct association.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT u FROM users u WHERE u.id = :id")
    Optional<User> findByIdForUpdate(Long id);
}
