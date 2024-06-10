package roomescape.exception;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import roomescape.payment.exception.PaymentFailException;

@RestControllerAdvice(annotations = RestController.class)
public class ApiControllerAdvice {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiControllerAdvice.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> IllegalArgExHandler(IllegalArgumentException exception) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST.getReasonPhrase(), exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MultipleErrorResponses<ErrorResponse>> methodArgumentExHandler(MethodArgumentNotValidException exception) {
        List<ErrorResponse> errorResponses = exception.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> new ErrorResponse(fieldError.getField(), fieldError.getDefaultMessage()))
                .toList();

        return ResponseEntity.badRequest()
                .body(new MultipleErrorResponses<>(errorResponses));
    }

    @ExceptionHandler(PaymentFailException.class)
    public ResponseEntity<ErrorResponse> paymentFailExHandler(PaymentFailException exception) {
        LOGGER.error("토스 결제 에러 | code : {} | message : {} |", exception.getCode(), exception.getMessage(), exception);

        return ResponseEntity.status(exception.getHttpStatus())
                .body(new ErrorResponse(exception.getCode(), exception.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> serverExHandler(Exception exception) {
        LOGGER.error("서버 에러 : {}", exception.getMessage(), exception);

        return ResponseEntity.internalServerError()
                .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                        "서버 에러입니다. 고객 센터에 문의 해주세요."));
    }
}
