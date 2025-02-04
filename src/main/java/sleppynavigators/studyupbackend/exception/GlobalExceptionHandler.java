package sleppynavigators.studyupbackend.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import sleppynavigators.studyupbackend.exception.business.BusinessExceptionBase;
import sleppynavigators.studyupbackend.exception.business.UnknownException;
import sleppynavigators.studyupbackend.exception.client.ClientExceptionBase;
import sleppynavigators.studyupbackend.exception.database.DatabaseExceptionBase;
import sleppynavigators.studyupbackend.exception.request.InvalidApiException;
import sleppynavigators.studyupbackend.exception.request.RequestExceptionBase;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(exception = {
            HttpMediaTypeNotSupportedException.class,
            HttpRequestMethodNotSupportedException.class,
            MissingServletRequestParameterException.class,
            NoResourceFoundException.class,
            HttpMessageNotReadableException.class,
            MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorResponse> handleNoResourceFoundException(
            HttpServletRequest request, NoResourceFoundException ignored) {
        return ErrorResponse.toResponseEntity(new InvalidApiException(), request.getRequestURI());
    }

    @ExceptionHandler(RequestExceptionBase.class)
    public ResponseEntity<ErrorResponse> handleRequestException(
            HttpServletRequest request, RequestExceptionBase exception) {
        return ErrorResponse.toResponseEntity(exception, request.getRequestURI());
    }

    @ExceptionHandler(BusinessExceptionBase.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
            HttpServletRequest request, BusinessExceptionBase exception) {
        log.error("Business exception : {}", exception.getMessage());
        return ErrorResponse.toResponseEntity(exception, request.getRequestURI());
    }

    @ExceptionHandler(ClientExceptionBase.class)
    public ResponseEntity<ErrorResponse> handleClientException(
            HttpServletRequest request, ClientExceptionBase exception) {
        log.error("Client exception : {}", exception.getMessage());
        return ErrorResponse.toResponseEntity(exception, request.getRequestURI());
    }

    @ExceptionHandler(DatabaseExceptionBase.class)
    public ResponseEntity<ErrorResponse> handleDatabaseException(
            HttpServletRequest request, DatabaseExceptionBase exception) {
        log.error("Database exception : {}", exception.getMessage());
        return ErrorResponse.toResponseEntity(exception, request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(HttpServletRequest request, Exception exception) {
        log.error("Unexpected exception : {}", exception.getMessage(), exception);
        return ErrorResponse.toResponseEntity(new UnknownException(), request.getRequestURI());
    }
}
