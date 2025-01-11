package sleppynavigators.studyupbackend.presentation.common;

import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

// TODO: complement exception handling
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(org.springframework.http.HttpStatus.BAD_REQUEST)
    public SU_Response<?> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException ignored) {
        return new SU_Response<>(SU_ResponseResult.BAD_REQUEST);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED)
    public SU_Response<?> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ignored) {
        return new SU_Response<>(SU_ResponseResult.QUERY_NOTFOUND);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(org.springframework.http.HttpStatus.BAD_REQUEST)
    public SU_Response<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException ignored) {
        return new SU_Response<>(SU_ResponseResult.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(org.springframework.http.HttpStatus.BAD_REQUEST)
    public SU_Response<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException ignored) {
        return new SU_Response<>(SU_ResponseResult.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(org.springframework.http.HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public SU_Response<?> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ignored) {
        return new SU_Response<>(SU_ResponseResult.BAD_REQUEST);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(org.springframework.http.HttpStatus.NOT_FOUND)
    public SU_Response<?> handleNoResourceFoundException(NoResourceFoundException ignored) {
        return new SU_Response<>(SU_ResponseResult.QUERY_NOTFOUND);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR)
    public SU_Response<?> handleException(Exception ignored) {
        // TODO: log exception
        return new SU_Response<>(SU_ResponseResult.INTERNAL_SERVER_ERROR);
    }
}
