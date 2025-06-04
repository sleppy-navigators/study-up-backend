package sleppynavigators.studyupbackend.infrastructure.challenge;

import com.querydsl.core.types.Predicate;
import java.util.List;
import sleppynavigators.studyupbackend.domain.challenge.Task;

public interface TaskQueryRepository {

    List<Task> findAll(Predicate predicate);

    List<Task> findAll(Predicate predicate, Long pageNum, Integer pageSize);
}
