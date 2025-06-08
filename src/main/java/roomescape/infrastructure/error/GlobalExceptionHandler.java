package roomescape.infrastructure.error;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import roomescape.infrastructure.error.exception.AuthInfoResolveException;
import roomescape.infrastructure.error.exception.ForbiddenException;
import roomescape.infrastructure.error.exception.JwtExtractException;
import roomescape.infrastructure.error.exception.LoginAuthException;
import roomescape.infrastructure.error.exception.MemberException;
import roomescape.infrastructure.error.exception.PaymentException;
import roomescape.infrastructure.error.exception.ReservationException;
import roomescape.infrastructure.error.exception.ReservationTimeException;
import roomescape.infrastructure.error.exception.ThemeException;
import roomescape.infrastructure.error.exception.UnauthorizedException;
import roomescape.infrastructure.error.exception.WaitingException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({
            ReservationTimeException.class,
            MemberException.class,
            ThemeException.class,
            WaitingException.class
    })
    public ResponseEntity<ApiFailResponse> handleWarningLevelDomainExceptions(final RuntimeException e) {
        log.warn("{}: {}", e.getClass().getSimpleName(), e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiFailResponse(e.getMessage()));
    }

    @ExceptionHandler({
            ReservationException.class,
            PaymentException.class
    })
    public ResponseEntity<ApiFailResponse> handleErrorLevelDomainExceptions(final RuntimeException e) {
        log.warn("{}: {}", e.getClass().getSimpleName(), e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiFailResponse(e.getMessage()));
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiFailResponse> handleForbiddenException(final ForbiddenException e) {
        log.warn("ForbiddenException: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ApiFailResponse("접근권한이 없습니다."));
    }

    @ExceptionHandler({
            JwtExtractException.class,
            AuthInfoResolveException.class})
    public ResponseEntity<ApiFailResponse> handleJwtErrors(final RuntimeException e) {
        log.warn("{}: {}", e.getClass().getSimpleName(), e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiFailResponse("인증 정보를 확인할 수 없습니다."));
    }

    @ExceptionHandler(LoginAuthException.class)
    public ResponseEntity<ApiFailResponse> handleLoginAuth(final LoginAuthException e) {
        log.warn("LoginAuthException: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiFailResponse("로그인에 실패했습니다. 아이디 또는 비밀번호를 다시 확인하세요"));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiFailResponse> handleUnauthorized(final UnauthorizedException e) {
        log.warn("UnauthorizedException: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiFailResponse("인증에 실패했습니다."));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiFailResponse> handleValidation(final MethodArgumentNotValidException e) {
        final String msg = e.getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("ValidationError: {}", msg);
        return ResponseEntity.badRequest().body(new ApiFailResponse(msg));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiFailResponse> handleNotReadable(final HttpMessageNotReadableException e) {
        if (e.getCause() instanceof final InvalidFormatException ex) {
            final Class<?> t = ex.getTargetType();
            if (t == LocalDate.class)
                return bad("날짜는 yyyy-MM-dd 형식이어야 합니다.");
            if (t == LocalTime.class)
                return bad("시간은 HH:mm 형식이어야 합니다.");
        }
        log.warn("HttpMessageNotReadable: {}", e.getMessage());
        return bad("잘못된 요청입니다.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiFailResponse> handleUnexpected(final Exception e) {
        log.error("Unexpected error", e);
        return ResponseEntity.internalServerError()
                .body(new ApiFailResponse("예상치 못한 에러가 발생했습니다."));
    }

    private ResponseEntity<ApiFailResponse> bad(final String msg) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiFailResponse(msg));
    }
}
