package roomescape.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import roomescape.controller.dto.response.ErrorMessageResponse;
import roomescape.global.exception.AuthorizationException;
import roomescape.global.exception.PaymentServerException;
import roomescape.global.exception.RoomescapeException;

@RestControllerAdvice
public class ControllerAdvice {
    private final Logger logger = LoggerFactory.getLogger(ControllerAdvice.class.getName());

    @ExceptionHandler(RoomescapeException.class)
    public ResponseEntity<ErrorMessageResponse> handleIllegalArgumentException(RoomescapeException e) {
        ErrorMessageResponse response = new ErrorMessageResponse(e.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<ErrorMessageResponse> handleAuthorizationException(AuthorizationException e) {
        ErrorMessageResponse response = new ErrorMessageResponse(e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessageResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e) {

        BindingResult bindingResult = e.getBindingResult();

        StringBuilder builder = new StringBuilder();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            builder.append("[");
            builder.append(fieldError.getField());
            builder.append("](은)는 ");
            builder.append(fieldError.getDefaultMessage());
        }

        ErrorMessageResponse response = new ErrorMessageResponse(builder.toString());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler({Exception.class, PaymentServerException.class})
    public ResponseEntity<ErrorMessageResponse> handleRuntimeException(Exception exception) {
        logger.error(exception.getMessage());
        exception.printStackTrace();

        ErrorMessageResponse errorMessageResponse = new ErrorMessageResponse("서버 내부 에러가 발생하였습니다.");
        return ResponseEntity.internalServerError().body(errorMessageResponse);
    }
}
