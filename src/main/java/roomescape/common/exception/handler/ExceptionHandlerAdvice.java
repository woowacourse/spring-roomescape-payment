package roomescape.common.exception.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import roomescape.common.exception.CustomException;
import roomescape.common.exception.dto.ErrorResponse;
import roomescape.common.exception.vo.ErrorCode;

@ControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        ErrorResponse response = new ErrorResponse(e.getErrorCode().status, e.getMessage());
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity.status(errorCode.status).body(response);
    }
}
