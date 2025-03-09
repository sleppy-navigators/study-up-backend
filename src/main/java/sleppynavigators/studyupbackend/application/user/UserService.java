package sleppynavigators.studyupbackend.application.user;

import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sleppynavigators.studyupbackend.domain.challenge.Task;
import sleppynavigators.studyupbackend.domain.group.Group;
import sleppynavigators.studyupbackend.infrastructure.challenge.TaskRepository;
import sleppynavigators.studyupbackend.infrastructure.group.GroupRepository;
import sleppynavigators.studyupbackend.presentation.group.dto.response.GroupListResponse;
import sleppynavigators.studyupbackend.presentation.user.dto.response.UserTaskListResponse;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class UserService {

    private final GroupRepository groupRepository;
    private final TaskRepository taskRepository;

    public GroupListResponse getGroups(Long userId) {
        List<Group> groups = groupRepository.findAllByMembersUserId(userId);
        return GroupListResponse.fromEntities(groups);
    }

    public UserTaskListResponse getTasks(Long userId) {
        List<Task> tasks = taskRepository.findAllByChallengeOwnerId(userId);
        return UserTaskListResponse.fromEntities(tasks);
    }
}
