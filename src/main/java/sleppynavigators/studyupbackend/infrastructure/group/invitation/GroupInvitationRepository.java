package sleppynavigators.studyupbackend.infrastructure.group.invitation;

import org.springframework.data.jpa.repository.JpaRepository;
import sleppynavigators.studyupbackend.domain.group.invitation.GroupInvitation;

public interface GroupInvitationRepository extends JpaRepository<GroupInvitation, Long> {
}
