package sleppynavigators.studyupbackend.infrastructure.group;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import sleppynavigators.studyupbackend.domain.group.GroupMember;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

    Optional<GroupMember> findByGroupIdAndUserId(Long groupId, Long userId);
}
