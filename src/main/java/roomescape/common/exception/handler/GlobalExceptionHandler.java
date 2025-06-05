package roomescape.common.exception.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.SocketTimeoutException;
import java.time.format.DateTimeParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;
import roomescape.common.exception.DuplicatedException;
import roomescape.common.exception.InUseException;
import roomescape.common.exception.NotFoundException;
import roomescape.common.exception.ValidationException;
import roomescape.common.security.exception.ForbiddenException;
import roomescape.common.security.exception.UnAuthorizedException;
import roomescape.payment.exception.PaymentClientException;
import roomescape.payment.exception.PaymentForbiddenException;
import roomescape.payment.exception.PaymentServerException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final ObjectMapper objectMapper;

    public GlobalExceptionHandler(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<String> handleDateTimeParseException(DateTimeParseException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<String> handleValidationException(ValidationException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(InUseException.class)
    public ResponseEntity<String> handleInUseException(InUseException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(UnAuthorizedException.class)
    public ResponseEntity<String> handleUnAuthorizedException(UnAuthorizedException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<String> handleForbiddenException(ForbiddenException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(NotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(DuplicatedException.class)
    public ResponseEntity<String> handleDuplicatedException(DuplicatedException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler(PaymentForbiddenException.class)
    public ResponseEntity<String> handlePaymentForbiddenException(PaymentForbiddenException e)
            throws JsonProcessingException {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(objectMapper.writeValueAsString(e));
    }

    @ExceptionHandler(PaymentClientException.class)
    public ResponseEntity<String> handlePaymentClientException(PaymentClientException e)
            throws JsonProcessingException {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectMapper.writeValueAsString(e));
    }

    @ExceptionHandler(PaymentServerException.class)
    public ResponseEntity<String> handlePaymentServerException(PaymentServerException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<String> handleResourceAccessException(ResourceAccessException e) {
        if (e.getCause() instanceof SocketTimeoutException) {
            return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body("요청 시간이 초과되었습니다.");
        }
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("서비스 연결에 문제가 발생하였습니다.");
    }
}
