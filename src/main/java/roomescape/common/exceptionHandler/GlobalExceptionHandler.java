package roomescape.common.exceptionHandler;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import roomescape.common.exception.ClientPaymentException;
import roomescape.common.exception.LoginException;
import roomescape.common.exception.PaymentException;
import roomescape.common.exceptionHandler.dto.ExceptionResponse;

import java.time.format.DateTimeParseException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final String EXCEPTION_PREFIX = "[ERROR] ";

    @ExceptionHandler(LoginException.class)
    public ResponseEntity<ExceptionResponse> loginFail(LoginException exception, HttpServletRequest request) {
        logException(exception, request, LogLevel.WARN);
        return createResponse(exception.getMessage(), request, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionResponse> invalidInput(IllegalArgumentException exception, HttpServletRequest request) {
        logException(exception, request, LogLevel.WARN);
        return createResponse(exception.getMessage(), request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ExceptionResponse> serverError(NullPointerException exception, HttpServletRequest request) {
        logException(exception, request, LogLevel.ERROR);
        return createResponse("서버의 오류입니다. 관리자에게 문의해주세요.", request, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<ExceptionResponse> noMatchTimeType(DateTimeParseException exception, HttpServletRequest request) {
        logException(exception, request, LogLevel.ERROR);
        return createResponse("요청 시간 형식이 맞지 않습니다.", request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionResponse> notReadable(HttpMessageNotReadableException exception, HttpServletRequest request) {
        Throwable rootCause = exception.getRootCause();
        if (rootCause instanceof IllegalArgumentException illegalArg) {
            logException(illegalArg, request, LogLevel.WARN);
            return createResponse(illegalArg.getMessage(), request, HttpStatus.BAD_REQUEST);
        }

        logException(exception, request, LogLevel.WARN);
        return createResponse("요청 입력이 잘못되었습니다.", request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ClientPaymentException.class)
    public ResponseEntity<ExceptionResponse> handleClientPaymentException(ClientPaymentException e, HttpServletRequest request) {
        logException(e, request, LogLevel.WARN);
        return createResponse(e.getMessage(), request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<ExceptionResponse> handlePaymentException(PaymentException e, HttpServletRequest request) {
        logException(e, request, LogLevel.ERROR);
        return createResponse(e.getMessage(), request, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> unknownException(Exception e, HttpServletRequest request) {
        logException(e, request, LogLevel.ERROR);
        return createResponse("예상치 못한 서버 오류입니다. 서버에 문의해주세요.", request, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ExceptionResponse> createResponse(String message, HttpServletRequest request, HttpStatus status) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(EXCEPTION_PREFIX + message, request.getRequestURI());
        return ResponseEntity.status(status).body(exceptionResponse);
    }

    private void logException(Exception e, HttpServletRequest request, LogLevel level) {
        String exceptionName = e.getClass().getSimpleName();
        String uri = request.getRequestURI();
        String message = e.getMessage();

        switch (level) {
            case WARN -> log.warn("{} 발생 - URI: {}, 메시지: {}", exceptionName, uri, message, e);
            case ERROR -> log.error("{} 발생 - URI: {}, 메시지: {}", exceptionName, uri, message, e);
        }
    }
}
