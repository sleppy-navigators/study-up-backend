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
public class ViewTaskDetailChatAction extends ChatAction {

    private Long challengeId;
    private Long taskId;

    public ViewTaskDetailChatAction(Long challengeId, Long taskId) {
        super(ChatActionType.VIEW_TASK_DETAIL);
        this.challengeId = challengeId;
        this.taskId = taskId;
    }
}
