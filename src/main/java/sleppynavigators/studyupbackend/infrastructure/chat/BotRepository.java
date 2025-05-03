package sleppynavigators.studyupbackend.infrastructure.chat;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import sleppynavigators.studyupbackend.domain.chat.Bot;

public interface BotRepository extends JpaRepository<Bot, Long> {
    Optional<Bot> findByGroupId(Long groupId);
}
