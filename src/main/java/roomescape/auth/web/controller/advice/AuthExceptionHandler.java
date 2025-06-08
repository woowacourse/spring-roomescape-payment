package roomescape.auth.web.controller.advice;

import static roomescape.auth.web.controller.response.AuthErrorCode.NOT_ADMIN;
import static roomescape.auth.web.controller.response.AuthErrorCode.NOT_AUTHORIZED;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import roomescape.auth.web.exception.NotAdminException;
import roomescape.auth.web.exception.NotAuthorizationException;
import roomescape.auth.web.exception.TokenNotFoundException;
import roomescape.global.response.ApiResponse;

@RestControllerAdvice
@Slf4j
public class AuthExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ApiResponse<Void>> handleNotAdminException(HttpServletRequest request,
                                                                     NotAdminException e) {
        log.info("{} | {} {}", request.getAttribute("traceId"), e.getClass(), e.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.fail(NOT_ADMIN));
    }

    @ExceptionHandler
    public ResponseEntity<ApiResponse<Void>> handleTokenNotFoundException(HttpServletRequest request,
                                                                          TokenNotFoundException e) {
        log.info("{} | {} {}", request.getAttribute("traceId"), e.getClass(), e.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.fail(NOT_AUTHORIZED));
    }

    @ExceptionHandler(value = NotAuthorizationException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotAuthorizationException(HttpServletRequest request,
                                                                             NotAuthorizationException e) {
        log.info("{} | {} {}", request.getAttribute("traceId"), e.getClass(), e.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.fail(NOT_AUTHORIZED));
    }
}
