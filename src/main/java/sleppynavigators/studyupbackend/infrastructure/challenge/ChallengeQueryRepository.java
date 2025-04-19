package sleppynavigators.studyupbackend.infrastructure.challenge;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import sleppynavigators.studyupbackend.domain.challenge.Challenge;

import java.util.List;

public interface ChallengeQueryRepository {

    List<Challenge> findAll(Predicate predicate, OrderSpecifier<?> orderSpecifier, Long pageNum, Integer pageSize);
}
