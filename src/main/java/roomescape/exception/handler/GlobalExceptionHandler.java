package roomescape.exception.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.*;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import roomescape.exception.custom.BadRequestException;
import roomescape.exception.custom.ConflictException;
import roomescape.exception.custom.ForbiddenException;
import roomescape.exception.custom.PaymentException;
import roomescape.exception.dto.ExceptionResponse;

@ControllerAdvice
public class GlobalExceptionHandler {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.warn(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionResponse("올바르지 않은 데이터 요청입니다."));
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ExceptionResponse> handleHandlerMethodValidationException(
            HandlerMethodValidationException e) {
        log.warn(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionResponse("올바르지 않은 데이터 요청입니다."));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ExceptionResponse> handleBadRequestException(Exception e) {
        log.error(e.getMessage());
        return ResponseEntity.badRequest()
                .body(new ExceptionResponse("잘못된 요청입니다."));
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ExceptionResponse> handleForbiddenExceptionException(Exception e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ExceptionResponse("허용되지 않는 요청입니다."));
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ExceptionResponse> handleConflictExceptionException(Exception e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ExceptionResponse("중복된 데이터 요청입니다."));
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<ExceptionResponse> handleRestClientExceptionException(RestClientException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ExceptionResponse("서버에 문제가 발생하였습니다. 잠시 후 다시 시도해 주세요."));
    }

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<ExceptionResponse> handlePaymentExceptionException(PaymentException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(e.getStatus())
                .body(new ExceptionResponse(e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception e) {
        log.error(e.getMessage());
        return ResponseEntity.internalServerError()
                .body(new ExceptionResponse("서버 관리자에게 문의하세요."));
    }
}