package sleppynavigators.studyupbackend.infrastructure.challenge;

import com.querydsl.core.types.Predicate;
import sleppynavigators.studyupbackend.domain.challenge.Task;

import java.util.List;

public interface TaskQueryRepository {

    List<Task> findAll(Predicate predicate, Long pageNum, Integer pageSize);
}
