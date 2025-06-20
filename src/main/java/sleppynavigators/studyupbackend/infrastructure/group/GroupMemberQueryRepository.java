package sleppynavigators.studyupbackend.infrastructure.group;

import java.util.List;
import sleppynavigators.studyupbackend.application.group.GroupMemberSortType;
import sleppynavigators.studyupbackend.presentation.group.dto.response.GroupMemberListResponse.GroupMemberListItem;

public interface GroupMemberQueryRepository {

    List<GroupMemberListItem> findByGroupIdWithSort(Long groupId, GroupMemberSortType sortType);
}
