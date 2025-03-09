package sleppynavigators.studyupbackend.infrastructure.group;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sleppynavigators.studyupbackend.domain.group.Group;

public interface GroupRepository extends JpaRepository<Group, Long> {

    // TODO: migrate to QueryDSL
    // TODO: sort by `Event`(challenge creation and task certification) utilizing `@SortDefault`
    @Query(nativeQuery = true, value = """
            SELECT g.* FROM `groups` g
            LEFT JOIN group_members gm ON g.id = gm.group_id
            WHERE gm.user_id = :userId
            """)
    List<Group> findAllByUserId(Long userId);
}
