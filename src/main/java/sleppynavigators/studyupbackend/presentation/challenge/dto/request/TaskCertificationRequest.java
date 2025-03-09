package sleppynavigators.studyupbackend.presentation.challenge.dto.request;

import jakarta.validation.constraints.NotNull;
import java.net.URL;
import java.util.List;

public record TaskCertificationRequest(@NotNull List<URL> externalLinks, @NotNull List<URL> imageUrls) {
}
