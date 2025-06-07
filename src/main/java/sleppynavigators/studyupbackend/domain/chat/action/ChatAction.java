package sleppynavigators.studyupbackend.domain.chat.action;

import org.springframework.http.HttpMethod;

public interface ChatAction {

    ChatActionType getType();

    String getUrl();

    HttpMethod getHttpMethod();
}
