package sleppynavigators.studyupbackend.infrastructure.challenge;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import sleppynavigators.studyupbackend.application.challenge.ChallengeSortType;
import sleppynavigators.studyupbackend.domain.challenge.QChallenge;
import sleppynavigators.studyupbackend.domain.challenge.QTask;

public class ChallengeQueryOptions {

    public static OrderSpecifier<?> getOrderSpecifier(ChallengeSortType sortType) {
        return switch (sortType) {
            case LATEST_CERTIFICATION -> new OrderSpecifier<>(Order.DESC,
                    JPAExpressions
                            .select(QTask.task.certification.certifiedAt.max())
                            .from(QTask.task)
                            .where(QChallenge.challenge.eq(QTask.task.challenge)));
            case NONE -> new OrderSpecifier<>(Order.ASC, QChallenge.challenge.id);
        };
    }

    public static BooleanExpression getGroupPredicate(Long groupId) {
        return QChallenge.challenge.group.id.eq(groupId);
    }
}
