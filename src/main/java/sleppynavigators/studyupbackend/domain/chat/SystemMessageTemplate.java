package sleppynavigators.studyupbackend.domain.chat;

import sleppynavigators.studyupbackend.domain.event.SystemEvent;

public enum SystemMessageTemplate {
    USER_JOIN_MESSAGE_TEMPLATE("%s님이 그룹에 참여했습니다."),
    USER_LEAVE_MESSAGE_TEMPLATE("%s님이 그룹을 나갔습니다."),
    CHALLENGE_CREATE_MESSAGE_TEMPLATE("%s님이 '%s' 챌린지를 생성했습니다."),
    CHALLENGE_COMPLETE_MESSAGE_TEMPLATE("%s님이 '%s' 챌린지를 완료했습니다.");

    private final String messageFormat;

    SystemMessageTemplate(String messageFormat) {
        this.messageFormat = messageFormat;
    }

    public String format(Object... args) {
        return String.format(messageFormat, args);
    }

    public static String generateMessage(SystemEvent event) {
        return switch (event.getType()) {
            case USER_JOIN -> event.generateMessage(USER_JOIN_MESSAGE_TEMPLATE);
            case USER_LEAVE -> event.generateMessage(USER_LEAVE_MESSAGE_TEMPLATE);
            case CHALLENGE_CREATE -> event.generateMessage(CHALLENGE_CREATE_MESSAGE_TEMPLATE);
            case CHALLENGE_COMPLETE -> event.generateMessage(CHALLENGE_COMPLETE_MESSAGE_TEMPLATE);
        };
    }
} 
