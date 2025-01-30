package sleppynavigators.studyupbackend.domain.user.vo;

import jakarta.persistence.Embeddable;

@Embeddable
public record UserProfile(String username, String email) {

    public UserProfile {
        validateUsername(username);
        validateEmail(email);
    }

    private void validateUsername(String username) {
        if (username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or blank");
        }
    }

    private void validateEmail(String email) {
        if (email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank");
        }

        if (!email.contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }
}
