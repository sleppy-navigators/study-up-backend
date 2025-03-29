package sleppynavigators.studyupbackend.presentation.challenge.dto.response;

import jakarta.validation.constraints.NotNull;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import sleppynavigators.studyupbackend.domain.challenge.vo.TaskCertification;

public record TaskCertificationDTO(@NotNull List<URL> externalLinks,
                                   @NotNull List<URL> imageUrls,
                                   LocalDateTime certificatedAt) {

    public static TaskCertificationDTO fromEntity(TaskCertification taskCertification) {
        return new TaskCertificationDTO(
                taskCertification.externalLinks(),
                taskCertification.imageUrls(),
                taskCertification.certifiedAt()
        );
    }
}
