package sleppynavigators.studyupbackend.common.support;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sleppynavigators.studyupbackend.domain.chat.ChatMessage;
import sleppynavigators.studyupbackend.domain.chat.SenderType;
import sleppynavigators.studyupbackend.domain.group.Group;
import sleppynavigators.studyupbackend.domain.group.invitation.GroupInvitation;
import sleppynavigators.studyupbackend.domain.user.User;
import sleppynavigators.studyupbackend.infrastructure.chat.ChatMessageRepository;
import sleppynavigators.studyupbackend.infrastructure.group.GroupRepository;
import sleppynavigators.studyupbackend.infrastructure.group.invitation.GroupInvitationRepository;

@Transactional
@Component
public class GroupSupport {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupInvitationRepository groupInvitationRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    public Group registerGroup(List<User> members) {
        Group group = Group.builder()
                .name("test-group")
                .description("test-group-description")
                .creator(members.get(0))
                .build();

        for (User member : members) {
            group.addMember(member);
        }
        return groupRepository.save(group);
    }

    public GroupInvitation registerGroupInvitation(Group group) {
        GroupInvitation groupInvitation = new GroupInvitation(group);
        return groupInvitationRepository.save(groupInvitation);
    }

    public List<ChatMessage> registerChatMessages(Group group, User sender, List<String> contents,
                                                  List<LocalDateTime> createdTimes) {
        List<ChatMessage> messages = new ArrayList<>();
        for (int i = 0; i < contents.size(); i++) {
            ChatMessage message = ChatMessage.builder()
                    .senderId(sender.getId())
                    .groupId(group.getId())
                    .content(contents.get(i))
                    .senderType(SenderType.USER)
                    .createdAt(createdTimes.get(i))
                    .build();
            messages.add(message);
        }
        return chatMessageRepository.saveAll(messages);
    }
}
