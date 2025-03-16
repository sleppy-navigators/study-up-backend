package sleppynavigators.studyupbackend.domain.chat;

import java.util.Arrays;
import java.util.Objects;

public enum SystemMessageTemplate {
    USER_JOIN(SystemMessageEvent.USER_JOIN, "%s님이 그룹에 참여했습니다.", 1),
    USER_LEAVE(SystemMessageEvent.USER_LEAVE, "%s님이 그룹을 나갔습니다.", 1),
    CHALLENGE_CREATE(SystemMessageEvent.CHALLENGE_CREATE, "%s님이 %s 챌린지를 생성했습니다.", 2),
    CHALLENGE_COMPLETE(SystemMessageEvent.CHALLENGE_COMPLETE, "%s님이 %s 챌린지를 완료했습니다.", 2);

    private final SystemMessageEvent event;
    private final String messageFormat;
    private final int expectedArgCount;

    SystemMessageTemplate(SystemMessageEvent event, String messageFormat, int expectedArgCount) {
        this.event = event;
        this.messageFormat = messageFormat;
        this.expectedArgCount = expectedArgCount;
    }

    public static SystemMessageTemplate from(SystemMessageEvent event) {
        return Arrays.stream(values())
                .filter(template -> template.event == event)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 이벤트입니다: " + event));
    }

    public String getMessage(String... args) {
        validateArgs(args);
        return String.format(messageFormat, (Object[]) args);
    }

    private void validateArgs(String[] args) {
        if (args == null || args.length != expectedArgCount) {
            throw new IllegalArgumentException(
                String.format("이 메시지는 %d개의 인자가 필요합니다. 받은 인자: %d", 
                    expectedArgCount, 
                    args == null ? 0 : args.length)
            );
        }

        if (Arrays.stream(args).anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("인자는 null이 될 수 없습니다.");
        }
    }
} 
