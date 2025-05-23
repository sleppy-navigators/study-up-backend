package sleppynavigators.studyupbackend.exception;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import sleppynavigators.studyupbackend.exception.business.BusinessBaseException;
import sleppynavigators.studyupbackend.exception.client.ClientBaseException;
import sleppynavigators.studyupbackend.exception.database.DatabaseBaseException;
import sleppynavigators.studyupbackend.exception.network.InvalidApiException;
import sleppynavigators.studyupbackend.exception.network.NetworkBaseException;

// According to the Open API v3 specification, a status code can only have one description,
// so for the sake of ease and uniformity of global error documentation,
// we'll only specify response schemes, etc. for the broadest fallback of Exception.
// See https://github.com/OAI/OpenAPI-Specification/blob/main/versions/3.0.3.md#patterned-fields-1
@Slf4j
@RestControllerAdvice
public class RestApiExceptionHandler {

    @ExceptionHandler(exception = {
            HttpMediaTypeNotSupportedException.class,
            HttpRequestMethodNotSupportedException.class,
            MissingServletRequestParameterException.class,
            NoResourceFoundException.class,
            HttpMessageNotReadableException.class,
            MethodArgumentNotValidException.class,
            HandlerMethodValidationException.class})
    public ResponseEntity<ErrorResponse> handleDefault4xxExceptions(
            HttpServletRequest request, Exception ex) {
        return ErrorResponse.toResponseEntity(new InvalidApiException(ex.getMessage()), request.getRequestURI());
    }

    @ExceptionHandler(NetworkBaseException.class)
    public ResponseEntity<ErrorResponse> handleRequestException(
            HttpServletRequest request, NetworkBaseException exception) {
        return ErrorResponse.toResponseEntity(exception, request.getRequestURI());
    }

    @ExceptionHandler(BusinessBaseException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
            HttpServletRequest request, BusinessBaseException exception) {
        log.error("Business exception : {}", exception.getMessage(), exception);
        return ErrorResponse.toResponseEntity(exception, request.getRequestURI());
    }

    @ExceptionHandler(ClientBaseException.class)
    public ResponseEntity<ErrorResponse> handleClientException(
            HttpServletRequest request, ClientBaseException exception) {
        log.error("Client exception : {}", exception.getMessage(), exception);
        return ErrorResponse.toResponseEntity(exception, request.getRequestURI());
    }

    @ExceptionHandler(DatabaseBaseException.class)
    public ResponseEntity<ErrorResponse> handleDatabaseException(
            HttpServletRequest request, DatabaseBaseException exception) {
        log.error("Database exception : {}", exception.getMessage());
        return ErrorResponse.toResponseEntity(exception, request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "잘못된 요청", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "401", description = "인증 오류", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "403", description = "권한 오류", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "404", description = "리소스 오류", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류", useReturnTypeSchema = true)
    })
    public ResponseEntity<ErrorResponse> handleException(HttpServletRequest request, Exception exception) {
        log.error("Unexpected exception : {}", exception.getMessage(), exception);
        return ErrorResponse.toResponseEntity(new UnknownException(), request.getRequestURI());
    }
}
