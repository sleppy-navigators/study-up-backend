package sleppynavigators.studyupbackend.infrastructure.challenge;

import com.querydsl.core.types.Predicate;
import sleppynavigators.studyupbackend.domain.challenge.Challenge;

import java.util.List;

public interface ChallengeQueryRepository {

    List<Challenge> findAll(Predicate predicate, Long pageNum, Integer pageSize);

    List<Challenge> findAllSortedByCertificationDate(Predicate predicate, Long pageNum, Integer pageSize);
}
