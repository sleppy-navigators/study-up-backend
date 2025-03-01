package sleppynavigators.studyupbackend.domain.challenge.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record ChallengeDescription(@Column String description) {

    private static final int MAX_LENGTH = 200;

    public ChallengeDescription {
        if (description != null && description.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "Challenge description must not be longer than " + MAX_LENGTH + " characters");
        }
    }
}
