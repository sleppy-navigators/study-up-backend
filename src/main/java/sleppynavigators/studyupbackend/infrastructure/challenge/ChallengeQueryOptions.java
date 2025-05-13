package sleppynavigators.studyupbackend.infrastructure.challenge;

import com.querydsl.core.types.dsl.BooleanExpression;
import java.time.LocalDateTime;
import sleppynavigators.studyupbackend.domain.challenge.QChallenge;

public class ChallengeQueryOptions {

    public static BooleanExpression getGroupPredicate(Long groupId) {
        QChallenge challenge = QChallenge.challenge;
        return challenge.group.id.eq(groupId);
    }

    public static BooleanExpression getCompletedAfterPredicate(LocalDateTime completedAt) {
        QChallenge challenge = QChallenge.challenge;
        return challenge.detail.deadline.after(completedAt);
    }
}
