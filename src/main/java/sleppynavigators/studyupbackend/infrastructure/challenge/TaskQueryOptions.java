package sleppynavigators.studyupbackend.infrastructure.challenge;

import com.querydsl.core.types.dsl.BooleanExpression;
import sleppynavigators.studyupbackend.application.challenge.TaskCertificationStatus;
import sleppynavigators.studyupbackend.domain.challenge.QTask;

import java.time.LocalDateTime;

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
            case ALL -> null;
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
}
