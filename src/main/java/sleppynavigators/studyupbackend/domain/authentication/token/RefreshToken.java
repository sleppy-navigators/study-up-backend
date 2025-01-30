package sleppynavigators.studyupbackend.domain.authentication.token;

import java.util.UUID;

public class RefreshToken {

    private final String value;

    public RefreshToken(String value) {
        validateValue(value);

        this.value = value;
    }

    public RefreshToken() {
        this(UUID.randomUUID().toString());
    }

    public static RefreshToken deserialize(String value) {
        return new RefreshToken(value);
    }

    public String serialize() {
        return value;
    }

    public RefreshToken rotate() {
        return new RefreshToken();
    }

    private void validateValue(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Value must not be null or empty");
        }
    }
}
