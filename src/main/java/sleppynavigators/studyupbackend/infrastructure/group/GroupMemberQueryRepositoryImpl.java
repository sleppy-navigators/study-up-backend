package sleppynavigators.studyupbackend.infrastructure.group;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import sleppynavigators.studyupbackend.application.group.GroupMemberSortType;
import sleppynavigators.studyupbackend.domain.challenge.QChallenge;
import sleppynavigators.studyupbackend.domain.challenge.QTask;
import sleppynavigators.studyupbackend.domain.challenge.hunting.QHunting;
import sleppynavigators.studyupbackend.domain.group.QGroupMember;
import sleppynavigators.studyupbackend.domain.user.QUser;
import sleppynavigators.studyupbackend.presentation.group.dto.response.GroupMemberListResponse.GroupMemberListItem;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupMemberQueryRepositoryImpl implements GroupMemberQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<GroupMemberListItem> findByGroupIdWithSort(Long groupId, GroupMemberSortType sortType) {
        QUser user = QUser.user;
        QHunting hunting = QHunting.hunting;
        QGroupMember groupMember = QGroupMember.groupMember;
        QChallenge challenge = QChallenge.challenge;
        QTask task = QTask.task;

        OrderSpecifier<?> orderSpecifier = switch (sortType) {
            case POINT -> user.point.amount.desc();
            case AVERAGE_CHALLENGE_COMPLETION_RATE -> calcAvgChallengeCompletionRate().desc();
            case HUNTING_COUNT -> hunting.count().desc();
            case NONE -> groupMember.id.asc();
        };

        return queryFactory
                .select(getGroupMemberListItemProjection())
                .from(groupMember)

                .join(groupMember.user, user)
                .leftJoin(challenge).on(challenge.owner.eq(user).and(challenge.group.id.eq(groupId)))
                .leftJoin(task).on(task.challenge.eq(challenge))
                .leftJoin(hunting).on(hunting.hunter.eq(user).and(hunting.target.challenge.group.id.eq(groupId)))

                .where(groupMember.group.id.eq(groupId))
                .groupBy(groupMember)
                .orderBy(orderSpecifier)
                .fetch();
    }

    private NumberExpression<Double> calcAvgChallengeCompletionRate() {
        QTask task = QTask.task;

        NumberExpression<Long> successfulTasksCount = new CaseBuilder()
                .when(task.certification.certifiedAt.isNotNull()).then(1L)
                .otherwise(0L)
                .sum();

        NumberExpression<Long> totalTasksCount = new CaseBuilder()
                .when(task.id.isNotNull()).then(1L)
                .otherwise(0L)
                .sum();

        return new CaseBuilder()
                .when(totalTasksCount.eq(0L)).then(0.0)
                .otherwise(successfulTasksCount.doubleValue().divide(totalTasksCount.doubleValue()));
    }

    private Expression<GroupMemberListItem> getGroupMemberListItemProjection() {
        QUser user = QUser.user;
        QHunting hunting = QHunting.hunting;

        return Projections.constructor(GroupMemberListItem.class,
                user.id,
                user.userProfile.username,
                user.point.amount,
                calcAvgChallengeCompletionRate(),
                hunting.count());
    }
}
