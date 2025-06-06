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
public class ViewMemberProfileChatAction extends ChatAction {

    private Long userId;

    public ViewMemberProfileChatAction(Long userId) {
        super(ChatActionType.VIEW_MEMBER_PROFILE);
        this.userId = userId;
    }
}
