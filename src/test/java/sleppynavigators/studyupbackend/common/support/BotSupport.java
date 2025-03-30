package sleppynavigators.studyupbackend.common.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sleppynavigators.studyupbackend.domain.bot.Bot;
import sleppynavigators.studyupbackend.domain.group.Group;
import sleppynavigators.studyupbackend.infrastructure.bot.BotRepository;
import sleppynavigators.studyupbackend.application.group.GroupService;
import sleppynavigators.studyupbackend.presentation.group.dto.request.GroupCreationRequest;

@Transactional
@Component
public class BotSupport {

    @Autowired
    private BotRepository botRepository;

    /**
     * <b>Caution!</b> This method do directly access the database. There's no consideration about side effects.
     *
     * @see GroupService#createGroup(Long, GroupCreationRequest)
     */
    public Bot registerBotToDB(Group groupToBelong) {
        Bot bot = new Bot(groupToBelong);
        return botRepository.save(bot);
    }
}
