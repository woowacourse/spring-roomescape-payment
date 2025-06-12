package roomescape.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.ResourceAccessException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value = CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        HttpStatusCode status = e.getStatus();
        if (status.is5xxServerError()) {
            logger.error("서버 에러 발생: message = {}, status = {}", e.getMessage(), e.getStatus());
        }
        if (status == HttpStatus.UNAUTHORIZED) {
            logger.warn("권한 없음: message = {}, status = {}", e.getMessage(), e.getStatus());
        }
        ErrorResponse body = new ErrorResponse(e.getMessage());
        return ResponseEntity.status(e.getStatus()).body(body);
    }

    @ExceptionHandler(value = ResourceAccessException.class)
    public ResponseEntity<ErrorResponse> handleSocketException(ResourceAccessException e) {
        logger.error("ResourceAccessException 발생: message={}", e.getMessage());
        ErrorResponse body = new ErrorResponse("연결에 실패했습니다.");
        return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body(body);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getAllErrors().getFirst().getDefaultMessage();
        logger.warn("MethodArgumentNotValidException 발생: message={}", errorMessage);
        ErrorResponse body = new ErrorResponse(errorMessage);
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(Exception e) {
        logger.warn("HttpMessageNotReadableException 발생: message={}", e.getMessage());
        ErrorResponse body = new ErrorResponse("잘못된 요청 형식입니다.");
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        logger.error("Exception 발생: message={}", e.getMessage(), e);
        ErrorResponse body = new ErrorResponse("오류가 발생하였습니다. 관리자에게 문의해주세요");
        return ResponseEntity.internalServerError().body(body);
    }
}
