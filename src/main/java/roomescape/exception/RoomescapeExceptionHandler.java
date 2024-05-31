package roomescape.exception;

import static roomescape.exception.RoomescapeExceptionType.INVALID_DATE_TIME_FORMAT;
import static roomescape.exception.RoomescapeExceptionType.NO_QUERY_PARAMETER;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import roomescape.dto.ErrorResponse;

@ControllerAdvice
public class RoomescapeExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handle(HttpMessageNotReadableException e) {
        e.printStackTrace();
        return ResponseEntity.status(INVALID_DATE_TIME_FORMAT.getStatus())
                .body(new ErrorResponse(INVALID_DATE_TIME_FORMAT.getMessage()));
    }

    @ExceptionHandler(RoomescapeException.class)
    public ResponseEntity<ErrorResponse> handle(RoomescapeException e) {
        e.printStackTrace();
        return ResponseEntity
                .status(e.getHttpStatus())
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handle(MissingServletRequestParameterException e) {
        e.printStackTrace();
        return ResponseEntity.status(NO_QUERY_PARAMETER.getStatus())
                .body(new ErrorResponse(NO_QUERY_PARAMETER.getMessage()));
    }

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<ErrorResponse> handle(PaymentException e) {
        e.printStackTrace();
        return ResponseEntity.status(e.getHttpStatusCode())
                .body(new ErrorResponse(e.getErrorMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handle(Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(500)
                .body(new ErrorResponse("예상치 못한 오류입니다. 서버 관계자에게 문의하세요."));
    }
}
