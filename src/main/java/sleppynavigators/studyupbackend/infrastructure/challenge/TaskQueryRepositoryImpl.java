package sleppynavigators.studyupbackend.infrastructure.challenge;

import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import sleppynavigators.studyupbackend.domain.challenge.QTask;
import sleppynavigators.studyupbackend.domain.challenge.Task;

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
}
