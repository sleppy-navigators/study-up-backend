package sleppynavigators.studyupbackend.presentation.group.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import sleppynavigators.studyupbackend.domain.group.GroupMember;

@Schema(description = "그룹 멤버 목록 응답")
public record GroupMemberListResponse(
        @Schema(description = "그룹 멤버 목록")
        @NotNull List<GroupMemberListItem> members
) {

    public static GroupMemberListResponse fromEntities(List<GroupMember> groupMembers) {
        return new GroupMemberListResponse(
                groupMembers.stream()
                        .map(GroupMemberListItem::fromEntity)
                        .toList());
    }

    @Schema(description = "그룹 멤버")
    public record GroupMemberListItem(
            @Schema(description = "멤버 ID", example = "1")
            @NotNull Long userId,

            @Schema(description = "멤버 이름", example = "홍길동")
            @NotBlank String userName,

            @Schema(description = "멤버 보유 포인트", example = "1000")
            @NotNull Long points,

            @Schema(description = "멤버 평균 챌린지 완수율", example = "85.5")
            @NotNull Double averageChallengeCompletionRate,

            @Schema(description = "멤버 헌팅 횟수", example = "5")
            @NotNull Long huntingCount
    ) {

        public static GroupMemberListItem fromEntity(GroupMember groupMember) {
            return new GroupMemberListItem(
                    groupMember.getUser().getId(),
                    groupMember.getUser().getUserProfile().getUsername(),
                    groupMember.getUser().getPoint().getAmount(),
                    groupMember.calcAvgChallengeCompletionRate(),
                    groupMember.calcHuntingCount().longValue()
            );
        }
    }
}
