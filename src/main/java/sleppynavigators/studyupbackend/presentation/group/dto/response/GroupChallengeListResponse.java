package sleppynavigators.studyupbackend.presentation.group.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import sleppynavigators.studyupbackend.domain.challenge.Challenge;
import sleppynavigators.studyupbackend.domain.challenge.Task;
import sleppynavigators.studyupbackend.presentation.challenge.dto.response.TaskCertificationDTO;

public record GroupChallengeListResponse(List<GroupChallengeListItem> challenges) {

    public record GroupChallengeListItem(Long id, String title, LocalDateTime deadline, String description,
                                         Long challengerId, String challengerName,
                                         TaskCertificationDTO recentCertification) {

        public static GroupChallengeListItem fromEntity(Challenge challenge) {
            Task recentCertifiedTask = challenge.getRecentCertifiedTask();

            return new GroupChallengeListItem(
                    challenge.getId(),
                    challenge.getTitle().title(),
                    challenge.getDeadline().deadline(),
                    challenge.getDescription(),
                    challenge.getOwner().getUser().getId(),
                    challenge.getOwner().getUser().getUserProfile().username(),
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
