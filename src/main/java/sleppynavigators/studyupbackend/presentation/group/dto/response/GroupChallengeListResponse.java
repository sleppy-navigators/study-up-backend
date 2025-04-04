package sleppynavigators.studyupbackend.presentation.group.dto.response;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import sleppynavigators.studyupbackend.domain.challenge.Challenge;
import sleppynavigators.studyupbackend.domain.challenge.Task;
import sleppynavigators.studyupbackend.presentation.challenge.dto.response.TaskCertificationDTO;

public record GroupChallengeListResponse(@NotNull @Valid List<GroupChallengeListItem> challenges) {

    public record GroupChallengeListItem(@NotNull Long id,
                                         @NotBlank String title,
                                         @NotNull LocalDateTime deadline,
                                         String description,
                                         @NotNull Long challengerId,
                                         @NotBlank String challengerName,
                                         @NotNull Boolean isCompleted,
                                         @NotNull Boolean currentlyJoined,
                                         @Valid TaskCertificationDTO recentCertification) {

        public static GroupChallengeListItem fromEntity(Challenge challenge) {
            Task recentCertifiedTask = challenge.getRecentCertifiedTask();

            return new GroupChallengeListItem(
                    challenge.getId(),
                    challenge.getDetail().title(),
                    challenge.getDetail().deadline(),
                    challenge.getDetail().description(),
                    challenge.getOwner().getId(),
                    challenge.getOwner().getUserProfile().username(),
                    challenge.isCompleted(),
                    challenge.getGroup().hasMember(challenge.getOwner()),
                    (recentCertifiedTask != null) ?
                            TaskCertificationDTO.fromEntity(recentCertifiedTask.getCertification())
                            : null
            );
        }
    }

    public static GroupChallengeListResponse fromEntities(List<Challenge> challenges) {
        return new GroupChallengeListResponse(
                challenges.stream()
                        .map(GroupChallengeListItem::fromEntity)
                        .toList()
        );
    }
}
