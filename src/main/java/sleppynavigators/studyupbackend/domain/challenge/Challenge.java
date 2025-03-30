package sleppynavigators.studyupbackend.domain.challenge;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
import sleppynavigators.studyupbackend.domain.challenge.vo.ChallengeDetail;
import sleppynavigators.studyupbackend.domain.common.TimeAuditBaseEntity;
import sleppynavigators.studyupbackend.domain.group.Group;
import sleppynavigators.studyupbackend.domain.user.User;
import sleppynavigators.studyupbackend.exception.business.ForbiddenContentException;

@SoftDelete
@Entity(name = "challenges")
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Challenge extends TimeAuditBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id", nullable = false, updatable = false)
    private User owner;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "group_id", nullable = false, updatable = false)
    private Group group;

    @Immutable
    @Embedded
    private ChallengeDetail detail;

    @OneToMany(mappedBy = "challenge", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Task> tasks;

    @Builder
    public Challenge(User owner, Group group, String title, String description, LocalDateTime deadline) {
        this.owner = owner;
        this.group = group;
        this.detail = new ChallengeDetail(title, deadline, description);
        this.tasks = new ArrayList<>();
    }

    public void addTask(String title, LocalDateTime deadline) {
        tasks.add(new Task(title, deadline, this));
    }

    public List<Task> getTasksForUser(User user) {
        if (!canAccess(user)) {
            throw new ForbiddenContentException();
        }

        return tasks;
    }

    public boolean canModify(User user) {
        return owner.equals(user);
    }

    public boolean canAccess(User user) {
        return group.hasMember(user);
    }

    public Task getRecentCertifiedTask() {
        return tasks.stream()
                .filter(Task::isSucceed)
                .max(Comparator.comparing(task -> task.getCertification().certifiedAt()))
                .orElse(null);
    }

    public boolean isAllTasksCompleted() {
       return tasks.stream().allMatch(Task::isCompleted);
    }

}