package sleppynavigators.studyupbackend.domain.challenge;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sleppynavigators.studyupbackend.domain.challenge.vo.Deadline;
import sleppynavigators.studyupbackend.domain.challenge.vo.ChallengeDescription;
import sleppynavigators.studyupbackend.domain.challenge.vo.Title;
import sleppynavigators.studyupbackend.domain.group.GroupMember;

@Entity(name = "challenges")
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Challenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private GroupMember owner;

    @Embedded
    private Title title;

    @Embedded
    private ChallengeDescription description;

    @Embedded
    private Deadline deadline;

    @OneToMany(mappedBy = "challenge", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Task> tasks;

    @Builder
    public Challenge(GroupMember owner, String title, String description, LocalDateTime deadline) {
        this.owner = owner;
        this.title = new Title(title);
        this.description = description != null ? new ChallengeDescription(description) : null;
        this.deadline = new Deadline(deadline);
        this.tasks = new ArrayList<>();
    }

    public void addTask(String title, LocalDateTime deadline) {
        tasks.add(new Task(title, deadline, this));
    }

    public boolean isDone() {
        return tasks.stream().allMatch(Task::isDone);
    }

    public Task getRecentCertifiedTask() {
        return tasks.stream()
                .filter(Task::isSucceed)
                .max(Comparator.comparing(task -> task.getCertification().certificateAt()))
                .orElse(null);
    }
}
