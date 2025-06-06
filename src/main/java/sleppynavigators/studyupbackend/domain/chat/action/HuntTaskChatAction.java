package sleppynavigators.studyupbackend.domain.chat.action;

import java.net.MalformedURLException;
import java.net.URL;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpMethod;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter(AccessLevel.PROTECTED)
public class HuntTaskChatAction extends ChatAction {

    private Long challengeId;
    private Long taskId;

    public HuntTaskChatAction(Long challengeId, Long taskId) {
        super(ChatActionType.HUNT_TASK);
        this.challengeId = challengeId;
        this.taskId = taskId;
    }

    @Override
    public URL getUrl() throws MalformedURLException {
        return new URL("/challenges/" + challengeId + "/tasks/" + taskId + "/hunt");
    }

    @Override
    public HttpMethod getHttpMethod() {
        return HttpMethod.POST;
    }
}
