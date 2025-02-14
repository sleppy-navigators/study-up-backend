package sleppynavigators.studyupbackend.application.group;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sleppynavigators.studyupbackend.domain.group.Group;
import sleppynavigators.studyupbackend.domain.group.GroupMember;
import sleppynavigators.studyupbackend.domain.user.User;
import sleppynavigators.studyupbackend.exception.database.EntityNotFoundException;
import sleppynavigators.studyupbackend.infrastructure.group.GroupMemberRepository;
import sleppynavigators.studyupbackend.infrastructure.group.GroupRepository;
import sleppynavigators.studyupbackend.infrastructure.user.UserRepository;
import sleppynavigators.studyupbackend.presentation.group.dto.GroupCreationRequest;
import sleppynavigators.studyupbackend.presentation.group.dto.GroupListResponse;
import sleppynavigators.studyupbackend.presentation.group.dto.GroupListResponse.GroupListItem;
import sleppynavigators.studyupbackend.presentation.group.dto.SimpleGroupResponse;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupService {

    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;

    public GroupListResponse getGroups(Long userId) {
        return new GroupListResponse(
                groupRepository.findByUserId(userId).stream().map(GroupListItem::fromEntity).toList());
    }

    @Transactional
    public SimpleGroupResponse createGroup(Long creatorId, GroupCreationRequest request) {
        User creator = userRepository.findById(creatorId).orElseThrow(EntityNotFoundException::new);
        Group savedGroup = groupRepository.save(request.toEntity(creator));
        return SimpleGroupResponse.fromEntity(savedGroup);
    }

    @Transactional
    public void leaveGroup(Long userId, Long groupId) {
        Group group = groupRepository.findById(groupId).orElseThrow(EntityNotFoundException::new);
        GroupMember targetMember = groupMemberRepository.findByGroupIdAndUserId(groupId, userId)
                .orElseThrow(EntityNotFoundException::new);

        group.removeMember(targetMember);
        if (!group.hasAnyMember()) {
            groupRepository.delete(group);
        }
    }
}
