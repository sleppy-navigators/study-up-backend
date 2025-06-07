package sleppynavigators.studyupbackend.application.event;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import sleppynavigators.studyupbackend.application.medium.MediumService;
import sleppynavigators.studyupbackend.domain.event.group.GroupCreateEvent;
import sleppynavigators.studyupbackend.domain.group.Group;
import sleppynavigators.studyupbackend.exception.database.EntityNotFoundException;
import sleppynavigators.studyupbackend.infrastructure.group.GroupRepository;

@Slf4j
@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupEventListener {

    private final GroupRepository groupRepository;
    private final MediumService mediumService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    // NOTE: Transaction in this method cannot be committed
    public void handleGroupCreateEvent(GroupCreateEvent event) {
        try {
            Group group = groupRepository.findById(event.groupId())
                    .orElseThrow(() -> new EntityNotFoundException("Group not found: " + event.groupId()));
            mediumService.storeMedia(group.getGroupDetail().getThumbnailUrl());
        } catch (Exception e) {
            log.error("Error handling GroupCreateEvent event: {}", e.getMessage(), e);
        }
    }
}
