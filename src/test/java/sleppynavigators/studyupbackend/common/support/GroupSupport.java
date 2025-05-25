package sleppynavigators.studyupbackend.common.support;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sleppynavigators.studyupbackend.application.chat.ChatMessageService;
import sleppynavigators.studyupbackend.application.group.GroupService;
import sleppynavigators.studyupbackend.domain.chat.ChatMessage;
import sleppynavigators.studyupbackend.domain.group.Group;
import sleppynavigators.studyupbackend.domain.group.invitation.GroupInvitation;
import sleppynavigators.studyupbackend.domain.user.User;
import sleppynavigators.studyupbackend.infrastructure.chat.ChatMessageRepository;
import sleppynavigators.studyupbackend.infrastructure.group.GroupRepository;
import sleppynavigators.studyupbackend.infrastructure.group.invitation.GroupInvitationRepository;
import sleppynavigators.studyupbackend.presentation.chat.dto.request.ChatMessageRequest;
import sleppynavigators.studyupbackend.presentation.group.dto.request.GroupCreationRequest;
import sleppynavigators.studyupbackend.presentation.group.dto.request.GroupInvitationAcceptRequest;
import sleppynavigators.studyupbackend.presentation.group.dto.response.GroupInvitationResponse;
import sleppynavigators.studyupbackend.presentation.group.dto.response.GroupResponse;

@Transactional
@Component
public class GroupSupport {

    @Autowired
    private GroupService groupService;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private GroupInvitationRepository groupInvitationRepository;

    @Autowired
    private GroupRepository groupRepository;

    public Group callToMakeGroup(List<User> members) {
        // Create a group with the first user as the creator
        User creator = members.get(0);
        GroupCreationRequest groupCreationRequest =
                new GroupCreationRequest("test-group", "test-group-description", null);
        GroupResponse groupResponse = groupService.createGroup(creator.getId(), groupCreationRequest);

        // Invite other users to the group
        GroupInvitationResponse invitationResponse = groupService.makeInvitation(groupResponse.id(), creator.getId());
        for (User member : members) {
            groupService.acceptInvitation(member.getId(), groupResponse.id(), invitationResponse.id(),
                    new GroupInvitationAcceptRequest(invitationResponse.invitationKey()));
        }

        return groupRepository.findById(groupResponse.id()).orElseThrow();
    }

    public GroupInvitation callToMakeInvitation(Group group, User inviter) {
        GroupInvitationResponse response = groupService.makeInvitation(group.getId(), inviter.getId());
        return groupInvitationRepository.findById(response.id()).orElseThrow();
    }

    public void callToLeaveGroup(User user, Long groupId) {
        groupService.leaveGroup(user.getId(), groupId);
    }

    /**
     * <b>Caution!</b> This method do directly access the database. There's no consideration about side effects.
     *
     * @see GroupService#createGroup(Long, GroupCreationRequest)
     */
    public Group registerGroupToDB(List<User> members) {
        User creator = members.get(0);
        Group group = Group.builder()
                .name("test-group")
                .description("test-group-description")
                .creator(creator)
                .build();

        for (User member : members) {
            group.addMember(member);
        }
        return groupRepository.save(group);
    }

    /**
     * <b>Caution!</b> This method do directly access the database. There's no consideration about side effects.
     *
     * @see ChatMessageService#sendSystemMessage(sleppynavigators.studyupbackend.domain.event.SystemMessageEvent)
     * @see ChatMessageService#sendUserMessage(ChatMessageRequest, String, Long)
     */
    public List<ChatMessage> registerChatMessagesToDB(Group group, User sender, List<String> contents) {
        List<ChatMessage> messages = new ArrayList<>();
        for (String content : contents) {
            ChatMessage message = ChatMessage.fromUser(sender.getId(), group.getId(), content);
            messages.add(chatMessageRepository.save(message));
        }
        return messages;
    }
}
