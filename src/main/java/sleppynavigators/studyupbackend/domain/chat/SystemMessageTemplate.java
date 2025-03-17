package sleppynavigators.studyupbackend.domain.chat;

import sleppynavigators.studyupbackend.domain.event.Event;
import sleppynavigators.studyupbackend.exception.business.InvalidPayloadException;

import java.util.Arrays;
import java.util.Objects;

import static sleppynavigators.studyupbackend.domain.event.Event.*;


public enum SystemMessageTemplate {

    USER_JOIN_MESSAGE_TEMPLATE(USER_JOIN, "%s님이 그룹에 참여했습니다.", 1),
    USER_LEAVE_MESSAGE_TEMPLATE(USER_LEAVE, "%s님이 그룹을 나갔습니다.", 1),
    CHALLENGE_CREATE_MESSAGE_TEMPLATE(CHALLENGE_CREATE, "%s님이 %s 챌린지를 생성했습니다.", 2),
    CHALLENGE_COMPLETE_MESSAGE_TEMPLATE(CHALLENGE_COMPLETE, "%s님이 %s 챌린지를 완료했습니다.", 2);

    private final Event event;
    private final String messageFormat;
    private final int expectedArgCount;

    SystemMessageTemplate(Event event, String messageFormat, int expectedArgCount) {
        this.event = event;
        this.messageFormat = messageFormat;
        this.expectedArgCount = expectedArgCount;
    }

    public static SystemMessageTemplate from(Event event) {
        return Arrays.stream(values())
                .filter(template -> template.event == event)
                .findFirst()
                .orElseThrow(() -> new InvalidPayloadException("지원하지 않는 이벤트입니다: " + event));
    }

    public String getMessage(String... args) {
        validateArgs(args);
        return String.format(messageFormat, (Object[]) args);
    }

    private void validateArgs(String[] args) {
        if (args == null || args.length != expectedArgCount) {
            throw new InvalidPayloadException(
                String.format("이 메시지는 %d개의 인자가 필요합니다. 받은 인자: %d", 
                    expectedArgCount, 
                    args == null ? 0 : args.length)
            );
        }

        if (Arrays.stream(args).anyMatch(Objects::isNull)) {
            throw new InvalidPayloadException("인자는 null이 될 수 없습니다.");
        }
    }
} 
