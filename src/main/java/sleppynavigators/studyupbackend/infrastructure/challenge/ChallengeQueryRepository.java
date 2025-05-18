package sleppynavigators.studyupbackend.infrastructure.challenge;

import com.querydsl.core.types.Predicate;
import java.util.List;
import sleppynavigators.studyupbackend.domain.challenge.Challenge;

public interface ChallengeQueryRepository {

    List<Challenge> findAll(Predicate predicate);

    List<Challenge> findAll(Predicate predicate, Long pageNum, Integer pageSize);

    List<Challenge> findAllSortedByCertificationDate(Predicate predicate, Long pageNum, Integer pageSize);
}
