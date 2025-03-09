package sleppynavigators.studyupbackend.infrastructure.group;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import sleppynavigators.studyupbackend.domain.group.Group;

public interface GroupRepository extends JpaRepository<Group, Long> {

    // TODO: sort by `Event`(challenge creation and task certification) utilizing `@SortDefault`
    List<Group> findAllByMembersUserId(Long userId);
}
