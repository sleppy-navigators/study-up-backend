package sleppynavigators.studyupbackend.domain.chat.action;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpMethod;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter(AccessLevel.PROTECTED)
public class ChatAction {

    private ChatActionType type;

    protected ChatAction(ChatActionType type) {
        this.type = type;
    }

    public final ChatActionType getType() {
        return type;
    }

    public String getUrl() {
        return null;
    }

    public HttpMethod getHttpMethod() {
        return null;
    }
}
