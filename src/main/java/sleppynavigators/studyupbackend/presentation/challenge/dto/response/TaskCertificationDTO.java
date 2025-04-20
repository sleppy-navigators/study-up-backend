package sleppynavigators.studyupbackend.presentation.challenge.dto.response;

import jakarta.validation.constraints.NotNull;

import java.net.URL;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import sleppynavigators.studyupbackend.domain.challenge.vo.TaskCertification;

public record TaskCertificationDTO(@NotNull List<URL> externalLinks,
                                   @NotNull List<URL> imageUrls,
                                   ZonedDateTime certificatedAt) {

    public static TaskCertificationDTO fromEntity(TaskCertification taskCertification) {
        return new TaskCertificationDTO(
                taskCertification.getExternalLinks(),
                taskCertification.getImageUrls(),
                taskCertification.getCertifiedAt().atZone(ZoneId.systemDefault())
        );
    }
}
