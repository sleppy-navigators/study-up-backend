package sleppynavigators.studyupbackend.infrastructure.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sleppynavigators.studyupbackend.domain.user.User;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT get_lock('userId_' + :userId, 2)")
    void lockById(Long userId);

    @Query("SELECT release_lock('userId_' + :userId)")
    void unlockById(Long userId);
}
