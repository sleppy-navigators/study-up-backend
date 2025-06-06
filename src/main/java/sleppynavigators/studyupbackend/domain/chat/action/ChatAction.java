package sleppynavigators.studyupbackend.domain.chat.action;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpMethod;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter(AccessLevel.PROTECTED)
public abstract class ChatAction {

    private ChatActionType type;

    protected ChatAction(ChatActionType type) {
        this.type = type;
    }

    public final ChatActionType getType() {
        return type;
    }

    public abstract String getUrl();

    public abstract HttpMethod getHttpMethod();
}
