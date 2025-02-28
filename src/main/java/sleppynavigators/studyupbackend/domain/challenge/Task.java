package sleppynavigators.studyupbackend.domain.challenge;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sleppynavigators.studyupbackend.domain.challenge.vo.TaskCertification;
import sleppynavigators.studyupbackend.domain.challenge.vo.Deadline;
import sleppynavigators.studyupbackend.domain.challenge.vo.Title;

@Entity(name = "tasks")
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Title title;

    @Embedded
    private Deadline deadline;

    @Embedded
    private TaskCertification certification;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id", nullable = false)
    private Challenge challenge;

    public Task(String title, LocalDateTime deadline, Challenge challenge) {
        this.title = new Title(title);
        this.deadline = new Deadline(deadline);
        this.challenge = challenge;
        this.certification = null;
    }

    public void certify(List<URL> externalLinks, List<URL> imageUrls) {
        this.certification = new TaskCertification(externalLinks, imageUrls, LocalDateTime.now());
    }

    public boolean isDone() {
        return isSucceed() || isFailed();
    }

    public boolean isSucceed() {
        return certification != null;
    }

    public boolean isFailed() {
        return !isSucceed() && deadline.isPast();
    }
}
