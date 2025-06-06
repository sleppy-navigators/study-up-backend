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
public class ShowOfChallengeChatAction extends ChatAction {

    private Long challengeId;

    public ShowOfChallengeChatAction(Long challengeId) {
        super(ChatActionType.SHOW_OF_CHALLENGE);
        this.challengeId = challengeId;
    }
}
