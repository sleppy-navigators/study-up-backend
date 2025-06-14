package sleppynavigators.studyupbackend.infrastructure.challenge;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import java.time.LocalDateTime;
import sleppynavigators.studyupbackend.application.challenge.TaskCertificationStatus;
import sleppynavigators.studyupbackend.domain.challenge.QTask;

public class TaskQueryOptions {

    public static BooleanExpression getStatusPredicate(TaskCertificationStatus status) {
        QTask task = QTask.task;
        return switch (status) {
            case SUCCEED -> task.certification.certifiedAt.isNotNull();
            case FAILED -> task.certification.certifiedAt.isNull()
                    .and(task.detail.deadline.loe(LocalDateTime.now()));
            case IN_PROGRESS -> task.certification.certifiedAt.isNull()
                    .and(task.detail.deadline.gt(LocalDateTime.now()));
            case COMPLETED -> task.certification.certifiedAt.isNotNull()
                    .or(task.detail.deadline.loe(LocalDateTime.now()));
            case ALL -> Expressions.asBoolean(true).isTrue();
        };
    }

    public static BooleanExpression getChallengePredicate(Long challengeId) {
        QTask task = QTask.task;
        return task.challenge.id.eq(challengeId);
    }

    public static BooleanExpression getGroupPredicate(Long groupId) {
        QTask task = QTask.task;
        return task.challenge.group.id.eq(groupId);
    }

    public static BooleanExpression getOwnerPredicate(Long ownerId) {
        QTask task = QTask.task;
        return task.challenge.owner.id.eq(ownerId);
    }

    public static BooleanExpression getCompletedBetweenPredicate(LocalDateTime start, LocalDateTime end) {
        QTask task = QTask.task;
        return task.detail.deadline.between(start, end);
    }
}
