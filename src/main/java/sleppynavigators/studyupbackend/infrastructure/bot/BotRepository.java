package sleppynavigators.studyupbackend.infrastructure.bot;

import org.springframework.data.jpa.repository.JpaRepository;
import sleppynavigators.studyupbackend.domain.bot.Bot;

import java.util.Optional;

public interface BotRepository extends JpaRepository<Bot, Long> {
    Optional<Bot> findByGroupId(Long groupId);
}
