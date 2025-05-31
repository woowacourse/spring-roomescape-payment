package roomescape.common.exception.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import roomescape.common.exception.CustomException;
import roomescape.common.exception.vo.ErrorCode;

// TODO: 예외 응답 객체 만들기
@ControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<String> handleCustomException(CustomException e) {
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity.status(errorCode.status).body(e.getMessage());
    }
}
