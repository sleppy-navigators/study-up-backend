package sleppynavigators.studyupbackend.presentation.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import sleppynavigators.studyupbackend.domain.user.User;
import sleppynavigators.studyupbackend.domain.user.following.Following;

@Schema(description = "팔로우 목록 응답")
public record FollowerListResponse(@NotNull List<UserResponse> followings,
                                   @NotNull List<UserResponse> followers) {

    public static FollowerListResponse fromEntity(User user) {
        return new FollowerListResponse(
                user.getFollowings().stream()
                        .map(Following::getFollowee)
                        .map(UserResponse::fromEntity)
                        .toList(),
                user.getFollowers().stream()
                        .map(Following::getFollower)
                        .map(UserResponse::fromEntity)
                        .toList()
        );
    }
}
