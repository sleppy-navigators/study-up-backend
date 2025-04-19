package sleppynavigators.studyupbackend.infrastructure.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import sleppynavigators.studyupbackend.domain.chat.Bot;

import java.util.Optional;

public interface BotRepository extends JpaRepository<Bot, Long> {
    Optional<Bot> findByGroupId(Long groupId);
}
