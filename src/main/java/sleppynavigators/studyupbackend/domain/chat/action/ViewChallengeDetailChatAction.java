package sleppynavigators.studyupbackend.domain.chat.action;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter(AccessLevel.PROTECTED)
public class ViewChallengeDetailChatAction extends ChatAction {

    private Long challengeId;

    public ViewChallengeDetailChatAction(Long challengeId) {
        super(ChatActionType.VIEW_CHALLENGE_DETAIL);
        this.challengeId = challengeId;
    }
}
