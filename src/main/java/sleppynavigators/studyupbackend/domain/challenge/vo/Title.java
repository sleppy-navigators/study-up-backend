package sleppynavigators.studyupbackend.domain.challenge.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record Title(@Column(nullable = false) String title) {

    private static final int MAX_LENGTH = 20;

    public Title {
        if (title.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Title must not be longer than " + MAX_LENGTH + " characters");
        }
    }
}
