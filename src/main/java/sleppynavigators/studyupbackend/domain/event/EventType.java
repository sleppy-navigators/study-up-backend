package sleppynavigators.studyupbackend.domain.event;

public enum EventType {
    // 시스템 메시지에서 사용될 이벤트 타입
    USER_JOIN,
    USER_LEAVE,
    CHALLENGE_CREATE,
    CHALLENGE_COMPLETE,
    CHALLENGE_CANCEL,
    GROUP_CREATE
}
