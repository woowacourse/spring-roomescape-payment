package roomescape.global.exception;

import java.net.SocketTimeoutException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import roomescape.global.exception.custom.BadRequestException;
import roomescape.global.exception.custom.ForbiddenException;
import roomescape.global.exception.custom.NotFoundException;
import roomescape.global.exception.custom.TossPaymentsException;
import roomescape.global.exception.custom.UnauthorizedException;
import roomescape.global.exception.dto.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TossPaymentsException.class)
    public ResponseEntity<ErrorResponse> handleTossPaymentsException(final TossPaymentsException e) {
        return ResponseEntity.status(e.getStatusCode().value())
                .body(new ErrorResponse(e.getMessage()));
    }

    /**
     * 400 Bad Request
     */
    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequestException(final BadRequestException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleJsonParseError() {
        return new ErrorResponse("요청 형식이 올바르지 않습니다.");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleTypeMismatch() {
        return new ErrorResponse("요청 파라미터의 타입이 올바르지 않습니다.");
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMissingParam(final MissingServletRequestParameterException e) {
        return new ErrorResponse(String.format("요청 파라미터 '%s'가 누락되었습니다.", e.getParameterName()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentException(final MethodArgumentNotValidException e) {
        return new ErrorResponse(e.getBindingResult().getFieldError().getDefaultMessage());
    }

    /**
     * 401 Unauthorized
     */
    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleUnauthorizedException(final UnauthorizedException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * 403 Forbidden
     */
    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public void handleForbiddenException(final ForbiddenException e) {
    }

    /**
     * 404 Not Found
     */
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final NotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * 500 Internal Server Error
     */
    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleDataAccessException(final DataAccessException e) {
        return new ErrorResponse("데이터베이스 오류가 발생했습니다.");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleIllegalArgumentException(final IllegalArgumentException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<ErrorResponse> handleSocketTimeoutException(final ResourceAccessException e) {
        Throwable cause = e.getCause();
        while (cause != null) {
            if (cause instanceof SocketTimeoutException) {
                return ResponseEntity.internalServerError().body(new ErrorResponse("시간이 초과되었습니다. 다시 시도해 주세요."));
            }
            cause = cause.getCause();
        }
        return ResponseEntity.internalServerError().body(new ErrorResponse("알 수 없는 에러가 발생했습니다."));
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleUncaughtException(final RuntimeException e) {
        e.printStackTrace();
        return new ErrorResponse("알 수 없는 에러가 발생했습니다.");
    }
}
