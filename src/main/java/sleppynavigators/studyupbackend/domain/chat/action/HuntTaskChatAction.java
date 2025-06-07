package sleppynavigators.studyupbackend.domain.chat.action;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpMethod;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter(AccessLevel.PROTECTED)
public class HuntTaskChatAction implements ChatAction {

    private Long challengeId;
    private Long taskId;

    public HuntTaskChatAction(Long challengeId, Long taskId) {
        this.challengeId = challengeId;
        this.taskId = taskId;
    }

    @Override
    public ChatActionType getType() {
        return ChatActionType.HUNT_TASK;
    }

    @Override
    public String getUrl() {
        // TODO: resolve presentation layer dependency
        return "/challenges/" + challengeId + "/tasks/" + taskId + "/hunt";
    }

    @Override
    public HttpMethod getHttpMethod() {
        return HttpMethod.POST;
    }
}
