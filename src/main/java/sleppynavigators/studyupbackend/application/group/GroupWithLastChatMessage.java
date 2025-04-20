package sleppynavigators.studyupbackend.application.group;

import sleppynavigators.studyupbackend.domain.chat.ChatMessage;
import sleppynavigators.studyupbackend.domain.group.Group;

public record GroupWithLastChatMessage(Group group, ChatMessage lastChatMessage) {
}
