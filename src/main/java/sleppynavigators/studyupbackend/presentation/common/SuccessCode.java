package sleppynavigators.studyupbackend.presentation.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SuccessCode {

    QUERY_OK(200, "S20", "Query Success"),
    ;

    private final int status;
    private final String code;
    private final String defaultMessage;
}
