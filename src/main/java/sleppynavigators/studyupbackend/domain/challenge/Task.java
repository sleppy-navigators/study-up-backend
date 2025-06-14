package sleppynavigators.studyupbackend.domain.challenge;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.SoftDelete;
import sleppynavigators.studyupbackend.domain.challenge.hunting.Hunting;
import sleppynavigators.studyupbackend.domain.challenge.vo.TaskCertification;
import sleppynavigators.studyupbackend.domain.challenge.vo.TaskDetail;
import sleppynavigators.studyupbackend.domain.common.TimeAuditBaseEntity;
import sleppynavigators.studyupbackend.domain.user.User;
import sleppynavigators.studyupbackend.exception.business.ForbiddenContentException;
import sleppynavigators.studyupbackend.exception.business.OveredDeadlineException;

@SoftDelete
@Entity(name = "tasks")
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Task extends TimeAuditBaseEntity {

    private static final double HUNTER_LIMIT_PER_TASK_RATIO = 0.3;

    @Immutable
    @Embedded
    private TaskDetail detail;

    @Embedded
    private TaskCertification certification;

    @Immutable
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "challenge_id", nullable = false)
    private Challenge challenge;

    @OneToMany(mappedBy = "target", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Hunting> huntings = new ArrayList<>();

    public Task(String title, LocalDateTime deadline, Challenge challenge) {
        this.detail = new TaskDetail(title, deadline);
        this.challenge = challenge;
        this.certification = new TaskCertification(new ArrayList<>(), new ArrayList<>(), null);
    }

    public void certify(List<URL> externalLinks, List<URL> imageUrls, User certifier) {
        if (!challenge.canModify(certifier)) {
            throw new ForbiddenContentException(
                    "User is not allowed to certify the task - userId: " + certifier.getId());
        }

        if (detail.isOverdue()) {
            throw new OveredDeadlineException("Task is overdue - taskId: " + getId());
        }

        this.certification = new TaskCertification(externalLinks, imageUrls, LocalDateTime.now());
    }

    public Long calcHuntingReward() {
        if (challenge.getTasks().isEmpty() || calcHuntingCountLimit() == 0) {
            return 0L;
        }

        return challenge.getDeposit().getInitialAmount() / challenge.getTasks().size() / calcHuntingCountLimit();
    }

    public Hunting hunt(User hunter) {
        if (!isHuntable()) {
            throw new ForbiddenContentException("Task is not huntable - taskId: " + getId());
        }

        if (!canHunt(hunter)) {
            throw new ForbiddenContentException("User is not allowed to hunt this task - userId: " + hunter.getId());
        }

        Hunting hunting = new Hunting(calcHuntingReward(), this, hunter);
        huntings.add(hunting);
        return hunting;
    }

    public boolean isSucceed() {
        return certification.isCertified();
    }

    public boolean isFailed() {
        return detail.isOverdue() && !certification.isCertified();
    }

    public boolean isAlreadyHunt(User user) {
        return huntings.stream()
                .anyMatch(hunting -> hunting.isHunter(user));
    }

    public boolean isHuntable() {
        return isFailed() && getHuntingCount() < calcHuntingCountLimit();
    }

    public boolean canHunt(User hunter) {
        return challenge.canAccess(hunter) && !challenge.isOwner(hunter) && !isAlreadyHunt(hunter);
    }

    private Integer getHuntingCount() {
        return huntings.size();
    }

    private Long calcHuntingCountLimit() {
        return Math.round(challenge.getGroup().getNumOfMembers() * HUNTER_LIMIT_PER_TASK_RATIO);
    }
}
