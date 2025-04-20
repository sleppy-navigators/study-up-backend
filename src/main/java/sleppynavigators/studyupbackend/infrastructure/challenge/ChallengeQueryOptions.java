package sleppynavigators.studyupbackend.infrastructure.challenge;

import com.querydsl.core.types.dsl.BooleanExpression;
import sleppynavigators.studyupbackend.domain.challenge.QChallenge;

public class ChallengeQueryOptions {

    public static BooleanExpression getGroupPredicate(Long groupId) {
        QChallenge challenge = QChallenge.challenge;
        return challenge.group.id.eq(groupId);
    }
}
