package sleppynavigators.studyupbackend.presentation.challenge.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import sleppynavigators.studyupbackend.domain.challenge.Challenge;

public record ChallengeResponse(@NotNull Long id,
                                @NotBlank String title,
                                @NotNull LocalDateTime deadline,
                                String description) {

    public static ChallengeResponse fromEntity(Challenge challenge) {
        return new ChallengeResponse(
                challenge.getId(),
                challenge.getDetail().title(),
                challenge.getDetail().deadline(),
                challenge.getDetail().description()
        );
    }
}
