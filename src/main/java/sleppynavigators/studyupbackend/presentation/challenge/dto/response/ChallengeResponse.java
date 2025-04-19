package sleppynavigators.studyupbackend.presentation.challenge.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import sleppynavigators.studyupbackend.domain.challenge.Challenge;

public record ChallengeResponse(@NotNull Long id,
                                @NotBlank String title,
                                @NotNull ZonedDateTime deadline,
                                String description) {

    public static ChallengeResponse fromEntity(Challenge challenge) {
        return new ChallengeResponse(
                challenge.getId(),
                challenge.getDetail().getTitle(),
                challenge.getDetail().getDeadline().atZone(ZoneId.systemDefault()),
                challenge.getDetail().getDescription()
        );
    }
}
