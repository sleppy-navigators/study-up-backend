package sleppynavigators.studyupbackend.infrastructure.challenge;

import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import sleppynavigators.studyupbackend.domain.challenge.Challenge;
import sleppynavigators.studyupbackend.domain.challenge.QChallenge;
import sleppynavigators.studyupbackend.domain.challenge.QTask;

import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ChallengeQueryRepositoryImpl implements ChallengeQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Challenge> findAll(Predicate predicate, Long pageNum, Integer pageSize) {
        QChallenge challenge = QChallenge.challenge;
        return queryFactory
                .selectFrom(challenge)
                .where(predicate)
                .offset(pageNum * pageSize)
                .limit(pageSize)
                .fetch();
    }

    @Override
    public List<Challenge> findAllSortedByCertificationDate(Predicate predicate, Long pageNum, Integer pageSize) {
        QChallenge challenge = QChallenge.challenge;
        QTask task = QTask.task;

        return queryFactory
                .selectFrom(challenge)
                .where(predicate)
                .leftJoin(task).on(task.challenge.eq(challenge))
                .groupBy(challenge.id)
                .orderBy(task.certification.certifiedAt.max().desc())
                .offset(pageNum * pageSize)
                .limit(pageSize)
                .fetch();
    }
}
