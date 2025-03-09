package sleppynavigators.studyupbackend.domain.group.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record GroupDetail(@Column(nullable = false) String name,
                          @Column(nullable = false) String description,
                          @Column String thumbnailUrl) {

    private static final int MAX_NAME_LENGTH = 20;
    private static final int MAX_DESCRIPTION_LENGTH = 200;

    public GroupDetail {
        validateName(name);
        validateDescription(description);
    }

    private void validateName(String name) {
        if (name.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException("Name must not be longer than " + MAX_NAME_LENGTH + " characters");
        }
    }

    private void validateDescription(String description) {
        if (description.length() > MAX_DESCRIPTION_LENGTH) {
            throw new IllegalArgumentException(
                    "Description must not be longer than " + MAX_DESCRIPTION_LENGTH + " characters");
        }
    }
}
