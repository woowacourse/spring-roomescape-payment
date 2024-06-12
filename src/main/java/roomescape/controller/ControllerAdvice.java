package roomescape.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import roomescape.controller.dto.ErrorMessageResponse;
import roomescape.global.exception.AuthorizationException;
import roomescape.global.exception.PaymentException;
import roomescape.global.exception.RoomescapeException;

@RestControllerAdvice
@Slf4j
public class ControllerAdvice {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorMessageResponse> handleRuntimeException(RuntimeException e) {
        log.error("Unexpected error occured: ", e);
        ErrorMessageResponse errorMessageResponse = new ErrorMessageResponse("서버 내부 에러가 발생하였습니다.");
        return ResponseEntity.internalServerError().body(errorMessageResponse);
    }

    @ExceptionHandler(RoomescapeException.class)
    public ResponseEntity<ErrorMessageResponse> handleRoomescapeException(RoomescapeException e) {
        log.warn("Exception(normal): {}", e.getMessage());
        ErrorMessageResponse response = new ErrorMessageResponse(e.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<ErrorMessageResponse> handlePaymentException(PaymentException e) {
        log.warn("Exception(pay): {}", e.getMessage());
        ErrorMessageResponse response = new ErrorMessageResponse(e.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<ErrorMessageResponse> handleAuthorizationException(AuthorizationException e) {
        log.warn("Exception(auth): {}", e.getMessage());
        ErrorMessageResponse response = new ErrorMessageResponse(e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessageResponse> handleMethodArgumentNotValidException(
        MethodArgumentNotValidException e
    ) {
        BindingResult bindingResult = e.getBindingResult();

        StringBuilder builder = new StringBuilder();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            builder.append("[");
            builder.append(fieldError.getField());
            builder.append("](은)는 ");
            builder.append(fieldError.getDefaultMessage());
        }

        log.warn("Exception(arg): {}", e.getMessage());
        ErrorMessageResponse response = new ErrorMessageResponse(builder.toString());
        return ResponseEntity.badRequest().body(response);
    }
}
