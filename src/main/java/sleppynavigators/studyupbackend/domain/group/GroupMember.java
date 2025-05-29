package sleppynavigators.studyupbackend.domain.group;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SoftDelete;
import sleppynavigators.studyupbackend.domain.challenge.Challenge;
import sleppynavigators.studyupbackend.domain.challenge.Task;
import sleppynavigators.studyupbackend.domain.common.TimeAuditBaseEntity;
import sleppynavigators.studyupbackend.domain.user.User;

@SoftDelete
@Entity(name = "group_members")
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class GroupMember extends TimeAuditBaseEntity {

    // There is room to add fields for group permissions in the future.
    // For now, we only need to know the group and the user.

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "group_id", nullable = false, updatable = false)
    private Group group;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    public GroupMember(Group group, User user) {
        this.group = group;
        this.user = user;
    }

    public Double calcAvgChallengeCompletionRate() {
        return group.getChallenges().stream()
                .filter(challenge -> challenge.isOwner(user))
                .filter(Challenge::isCompleted)
                .mapToDouble(Challenge::calcCompletionRate)
                .average()
                .orElse(0.0);
    }

    public Integer calcHuntingCount() {
        return group.getChallenges().stream()
                .filter(challenge -> !challenge.isOwner(user))
                .flatMap(challenge -> challenge.getTasks().stream())
                .filter(Task::isFailed)
                .filter(task -> task.isHunter(user))
                .toList()
                .size();
    }
}
