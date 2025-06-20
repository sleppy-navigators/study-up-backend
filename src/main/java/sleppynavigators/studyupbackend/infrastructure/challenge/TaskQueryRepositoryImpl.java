package sleppynavigators.studyupbackend.infrastructure.challenge;

import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import sleppynavigators.studyupbackend.domain.challenge.QChallenge;
import sleppynavigators.studyupbackend.domain.challenge.QTask;
import sleppynavigators.studyupbackend.domain.challenge.Task;
import sleppynavigators.studyupbackend.domain.group.QGroup;
import sleppynavigators.studyupbackend.domain.challenge.hunting.QHunting;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class TaskQueryRepositoryImpl implements TaskQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Task> findAll(Predicate predicate) {
        QTask task = QTask.task;
        return queryFactory
                .selectFrom(task)
                .where(predicate)
                .fetch();
    }

    @Override
    public List<Task> findAll(Predicate predicate, Long pageNum, Integer pageSize) {
        QTask task = QTask.task;
        return queryFactory
                .selectFrom(task)
                .where(predicate)
                .offset(pageNum * pageSize)
                .limit(pageSize)
                .fetch();
    }

    @Override
    public List<Task> findHuntableTasks(Long userId, Integer pageSize) {
        QTask task = QTask.task;
        QChallenge challenge = QChallenge.challenge;
        QHunting hunting = QHunting.hunting;
        QGroup group = QGroup.group;

        // TODO: consider de-normalization
        // The current structure duplicates business processes in both the application and data access layers.
        // This leaves room for future issues, such as inconsistency.
        return queryFactory
                .selectFrom(task)
                .join(task.challenge, challenge)
                .join(challenge.group, group)
                .where(
                        // isFailed(): deadline is over and not certified
                        task.detail.deadline.loe(LocalDateTime.now()),
                        task.certification.certifiedAt.isNull(),

                        // canHunt(user): user can access, not owner, and not already hunted
                        challenge.group.members.any().user.id.eq(userId),
                        challenge.owner.id.ne(userId),
                        queryFactory
                                .selectOne()
                                .from(hunting)
                                .where(hunting.target.eq(task).and(hunting.hunter.id.eq(userId)))
                                .notExists(),

                        // isHuntable(): hunting count is less than the limit
                        task.huntings.size().lt(challenge.group.members.size().doubleValue().multiply(0.3).round()))
                .limit(pageSize)
                .fetch();
    }
}
