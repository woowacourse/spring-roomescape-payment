package roomescape.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import roomescape.common.exception.impl.BadRequestException;
import roomescape.common.exception.impl.ConflictException;
import roomescape.common.exception.impl.ForbiddenException;
import roomescape.common.exception.impl.NotFoundException;
import roomescape.common.exception.impl.UnauthorizedException;
import roomescape.payment.infrastructure.TossPaymentException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handle(final Exception e) {
        log.error("handleException: {}", e.getMessage(), e);
        return new ResponseEntity<>("서버 내부에 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<String> handle(final BadRequestException e) {
        log.info("handleBadRequestException: {}", e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handle(final NotFoundException e) {
        log.info("handleNotFoundException: {}", e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<String> handle(final ConflictException e) {
        log.info("handleConflictException: {}", e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<String> handle(final UnauthorizedException e) {
        log.info("handleUnauthorizedException: {}", e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<String> handle(final ForbiddenException e) {
        log.info("handleForbiddenException: {}", e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handle(final MethodArgumentNotValidException e) {
        log.info("handleMethodArgumentNotValidException: {}", e.getMessage());
        return new ResponseEntity<>("잘못된 요청입니다.", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TossPaymentException.class)
    public ResponseEntity<String> handle(final TossPaymentException e) {
        log.info("handleTossPaymentException: {}", e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
