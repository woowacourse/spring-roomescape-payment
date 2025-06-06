package roomescape.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SeparatedMessageException.class)
    public ResponseEntity<ErrorResponse> handle(SeparatedMessageException e) {
        log.warn("Exception: message={}", e.getMessage());
        ErrorResponse response = new ErrorResponse(e.getStatus(), e.getClientMessage());
        return ResponseEntity.status(e.getStatus()).body(response);
    }

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ErrorResponse> handle(ApplicationException e) {
        log.warn("Exception: message={}", e.getMessage());
        ErrorResponse response = new ErrorResponse(e.getStatus(), e.getMessage());
        return ResponseEntity.status(e.getStatus()).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handle(Exception e) {
        log.error("Exception: message={}", e.getMessage(), e);
        ErrorResponse response = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "오류가 발생하였습니다. 관리자에게 문의해주세요");
        return ResponseEntity.internalServerError().body(response);
    }
}
