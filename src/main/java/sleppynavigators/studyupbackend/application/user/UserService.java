package sleppynavigators.studyupbackend.application.user;

import java.util.List;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sleppynavigators.studyupbackend.domain.challenge.Task;
import sleppynavigators.studyupbackend.domain.chat.ChatMessage;
import sleppynavigators.studyupbackend.domain.group.Group;
import sleppynavigators.studyupbackend.domain.user.User;
import sleppynavigators.studyupbackend.exception.database.EntityNotFoundException;
import sleppynavigators.studyupbackend.infrastructure.challenge.TaskRepository;
import sleppynavigators.studyupbackend.infrastructure.chat.ChatMessageRepository;
import sleppynavigators.studyupbackend.infrastructure.group.GroupRepository;
import sleppynavigators.studyupbackend.infrastructure.user.UserRepository;
import sleppynavigators.studyupbackend.presentation.group.dto.response.GroupListResponse;
import sleppynavigators.studyupbackend.presentation.user.dto.response.UserResponse;
import sleppynavigators.studyupbackend.presentation.user.dto.response.UserTaskListResponse;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class UserService {

    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final TaskRepository taskRepository;
    private final ChatMessageRepository chatMessageRepository;

    public UserResponse getUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found - userId: " + userId));
        return UserResponse.fromEntity(user);
    }

    public GroupListResponse getGroups(Long userId) {
        List<Group> groups = groupRepository.findAllByMembersUserId(userId);
        List<ChatMessage> chatMessages = chatMessageRepository
                .findLatestMessagesPerGroupByGroupIds(groups.stream().map(Group::getId).toList());
        return GroupListResponse.fromEntities(groups, chatMessages);
    }

    public UserTaskListResponse getTasks(Long userId) {
        List<Task> tasks = taskRepository.findAllByChallengeOwnerId(userId);
        return UserTaskListResponse.fromEntities(tasks);
    }
}
