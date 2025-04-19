package sleppynavigators.studyupbackend.infrastructure.challenge;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import sleppynavigators.studyupbackend.domain.challenge.Challenge;
import sleppynavigators.studyupbackend.domain.challenge.QChallenge;

import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ChallengeQueryRepositoryImpl implements ChallengeQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Challenge> findAll(Predicate predicate, OrderSpecifier<?> orderSpecifier,
                                   Long pageNum, Integer pageSize) {
        return queryFactory
                .selectFrom(QChallenge.challenge)
                .where(predicate)
                .orderBy(orderSpecifier)
                .offset(pageNum * pageSize)
                .limit(pageSize)
                .fetch();
    }
}
