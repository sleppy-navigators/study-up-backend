package sleppynavigators.studyupbackend.infrastructure.challenge.hunting;

import org.springframework.data.jpa.repository.JpaRepository;
import sleppynavigators.studyupbackend.domain.challenge.hunting.Hunting;

public interface HuntingRepository extends JpaRepository<Hunting, Long> {
}
