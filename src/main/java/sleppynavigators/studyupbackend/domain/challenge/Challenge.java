package sleppynavigators.studyupbackend.domain.challenge;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.SoftDelete;
import sleppynavigators.studyupbackend.domain.challenge.hunting.vo.Deposit;
import sleppynavigators.studyupbackend.domain.challenge.vo.ChallengeDetail;
import sleppynavigators.studyupbackend.domain.common.TimeAuditBaseEntity;
import sleppynavigators.studyupbackend.domain.group.Group;
import sleppynavigators.studyupbackend.domain.point.vo.Point;
import sleppynavigators.studyupbackend.domain.user.User;
import sleppynavigators.studyupbackend.exception.business.ChallengeInProgressException;

@SoftDelete
@Entity(name = "challenges")
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Challenge extends TimeAuditBaseEntity {

    private static final long MODIFIABLE_PERIOD_HOUR = 24L;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id", nullable = false, updatable = false)
    private User owner;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "group_id", nullable = false, updatable = false)
    private Group group;

    @Immutable
    @Embedded
    private ChallengeDetail detail;

    @Embedded
    private Deposit deposit;

    @OneToMany(mappedBy = "challenge", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Task> tasks;

    @Builder
    public Challenge(User owner, Group group, String title, String description, Long deposit) {
        this.owner = owner;
        this.group = group;
        this.detail = new ChallengeDetail(title, description);
        this.deposit = new Deposit(deposit);
        this.tasks = new ArrayList<>();
    }

    public void addTask(String title, LocalDateTime deadline) {
        tasks.add(new Task(title, deadline, this));
        if (detail.getDeadline() == null || deadline.isAfter(detail.getDeadline())) {
            detail = new ChallengeDetail(detail.getTitle(), deadline, detail.getDescription());
        }
    }

    public boolean isOwner(User user) {
        return owner.equals(user);
    }

    public boolean canModify(User user) {
        // We might change it to allow edits even if the user is not the owner of the challenge.
        return isOwner(user) &&
                LocalDateTime.now().isBefore(getCreatedAt().plusHours(MODIFIABLE_PERIOD_HOUR));
    }

    public boolean canAccess(User user) {
        return group.hasMember(user);
    }

    public void rewardToHunter(Long reward, User hunter) {
        deposit = deposit.subtract(reward);
        hunter.grantPoint(reward);
    }

    public void rewardToOwner() {
        if (!isCompleted()) {
            throw new ChallengeInProgressException();
        }

        Point reward = deposit.calculateReward();
        owner.grantPoint(reward.getAmount());
    }

    public Task getRecentCertifiedTask() {
        return tasks.stream()
                .filter(Task::isSucceed)
                .max(Comparator.comparing(task -> task.getCertification().getCertifiedAt()))
                .orElse(null);
    }

    public boolean isCompleted() {
        return detail.isOverdue();
    }

    public double calcSuccessRate() {
        if (tasks.isEmpty()) {
            return 0.0;
        }

        long completedTasks = tasks.stream().filter(Task::isSucceed).count();
        return (double) completedTasks / tasks.size() * 100;
    }

    public LocalDateTime getDeadline() {
        return detail.getDeadline();
    }
}
