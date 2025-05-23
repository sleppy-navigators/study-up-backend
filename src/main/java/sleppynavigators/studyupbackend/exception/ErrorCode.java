package sleppynavigators.studyupbackend.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public enum ErrorCode {

    // request exceptions
    INVALID_API(400, "R40", "Bad request"),
    UNAUTHORIZED(401, "R41", "Unauthorized"),
    FORBIDDEN(403, "R43", "Forbidden"),
    INVALID_CREDENTIALS(400, "R44", "Invalid credentials"),

    // database exceptions
    ENTITY_NOT_FOUND(404, "D44", "Not found"),

    // client exceptions
    INITIALIZE_FAILED(500, "C50", "Client initialization failed"),
    UNSUCCESSFUL_RESPONSE(500, "C52", "Internal server error"),

    // business exceptions
    SESSION_EXPIRED(400, "B40", "Session expired"),
    CHAT_MESSAGE(400, "B41", "Chat message error"),
    INVALID_PAYLOAD(400, "B42", "Invalid payload"),
    OVERED_DEADLINE(400, "B43", "Deadline was overed"),
    FORBIDDEN_CONTENT(400, "B44", "Forbidden content"),
    ACTION_REQUIRED_BEFORE(400, "B45", "Some actions required before"),
    CHALLENGE_IN_PROGRESS(400, "B46", "Challenge in progress"),
    INSUFFICIENT_POINTS(400, "B47", "Insufficient points"),

    // global exceptions
    INTERNAL_SERVER_ERROR(500, "G50", "Internal server error"),
    ;

    private final int status;
    private final String code;
    private final String defaultMessage;
}
