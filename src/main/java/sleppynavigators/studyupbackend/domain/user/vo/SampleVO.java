package sleppynavigators.studyupbackend.domain.user.vo;

import jakarta.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public record SampleVO(String message) {
    public SampleVO {
        Objects.requireNonNull(message);
    }
}
