package roomescape.waiting.controller.exception;

import static org.springframework.http.HttpStatus.CONFLICT;
import static roomescape.waiting.controller.response.WaitingErrorCode.IN_ALREADY_WAITING;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import roomescape.global.response.ApiResponse;
import roomescape.waiting.exception.InAlreadyWaitingException;

@RestControllerAdvice
@Slf4j
public class WaitingExceptionHandler {

    @ExceptionHandler(InAlreadyWaitingException.class)
    public ResponseEntity<ApiResponse<Void>> handleInAlreadyWaitingException(HttpServletRequest request,
                                                                             InAlreadyWaitingException e) {
        log.info("{} | {} {}", request.getAttribute("traceId"), e.getClass(), e.getMessage());
        return ResponseEntity
                .status(CONFLICT)
                .body(ApiResponse.fail(IN_ALREADY_WAITING));
    }
}
