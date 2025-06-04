package sleppynavigators.studyupbackend.domain.group.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.net.URL;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter(AccessLevel.PROTECTED)
public class GroupDetail {

    private static final int MAX_NAME_LENGTH = 20;
    private static final int MAX_DESCRIPTION_LENGTH = 200;

    @Column(nullable = false)
    String name;

    @Column(nullable = false)
    String description;

    @Column
    URL thumbnailUrl;

    public GroupDetail(String name, String description, URL thumbnailUrl) {
        validateName(name);
        validateDescription(description);

        this.name = name;
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;
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
