package sleppynavigators.studyupbackend.infrastructure.group;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sleppynavigators.studyupbackend.domain.group.Group;

public interface GroupRepository extends JpaRepository<Group, Long> {

    @Query("SELECT g FROM user_groups g JOIN g.members m WHERE m.user.id = :userId")
    List<Group> findByUserId(Long userId);
}
