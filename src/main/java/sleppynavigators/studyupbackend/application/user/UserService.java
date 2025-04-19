package sleppynavigators.studyupbackend.application.user;

import java.util.List;
import java.util.Map;

import com.querydsl.core.types.Predicate;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sleppynavigators.studyupbackend.application.group.GroupChatMessageHelper;
import sleppynavigators.studyupbackend.domain.challenge.Task;
import sleppynavigators.studyupbackend.domain.chat.ChatMessage;
import sleppynavigators.studyupbackend.domain.group.Group;
import sleppynavigators.studyupbackend.domain.user.User;
import sleppynavigators.studyupbackend.exception.database.EntityNotFoundException;
import sleppynavigators.studyupbackend.infrastructure.challenge.TaskQueryOptions;
import sleppynavigators.studyupbackend.infrastructure.challenge.TaskRepository;
import sleppynavigators.studyupbackend.infrastructure.chat.ChatMessageRepository;
import sleppynavigators.studyupbackend.infrastructure.group.GroupRepository;
import sleppynavigators.studyupbackend.infrastructure.user.UserRepository;
import sleppynavigators.studyupbackend.presentation.challenge.dto.request.TaskSearch;
import sleppynavigators.studyupbackend.presentation.group.dto.request.GroupSearch;
import sleppynavigators.studyupbackend.presentation.group.dto.response.GroupDTO;
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

    public GroupListResponse getGroups(Long userId, GroupSearch search) {
        List<Group> groups = groupRepository.findAllByMembersUserId(userId);
        List<ChatMessage> chatMessages = chatMessageRepository
                .findLatestGroupMessages(groups.stream().map(Group::getId).toList());

        Map<Group, ChatMessage> groupToLastChatMessage = GroupChatMessageHelper
                .aggregateGroupWithFirstChatMessage(groups, chatMessages);
        List<GroupDTO> groupDTOs = GroupChatMessageHelper
                .convertAndSortToGroupDTOs(groupToLastChatMessage, search.sortBy());
        return new GroupListResponse(groupDTOs);
    }

    public UserTaskListResponse getTasks(Long userId, TaskSearch search) {
        Predicate predicate = TaskQueryOptions.getOwnerPredicate(userId)
                .and(TaskQueryOptions.getStatusPredicate(search.status()));
        List<Task> tasks = taskRepository.findAll(predicate, search.pageNum(), search.pageSize());
        return UserTaskListResponse.fromEntities(tasks);
    }
}
