package roomescape.infrastructure.error;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
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
import roomescape.infrastructure.log.ErrorLog;
import roomescape.infrastructure.log.LogEntry;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiFailResponse> handleForbiddenException(ForbiddenException ex) {
        LOGGER.error(buildLogEntry(ex).toLogMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiFailResponse("접근권한이 없습니다."));
    }

    @ExceptionHandler(ReservationException.class)
    public ResponseEntity<ApiFailResponse> handleReservationException(ReservationException ex) {
        LOGGER.error(buildLogEntry(ex).toLogMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiFailResponse(ex.getMessage()));
    }

    @ExceptionHandler(ReservationTimeException.class)
    public ResponseEntity<ApiFailResponse> handleReservationTimeException(ReservationTimeException ex) {
        LOGGER.error(buildLogEntry(ex).toLogMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiFailResponse(ex.getMessage()));
    }

    @ExceptionHandler(MemberException.class)
    public ResponseEntity<ApiFailResponse> handleMemberException(MemberException ex) {
        LOGGER.error(buildLogEntry(ex).toLogMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiFailResponse(ex.getMessage()));
    }

    @ExceptionHandler(ThemeException.class)
    public ResponseEntity<ApiFailResponse> handleThemeException(ThemeException ex) {
        LOGGER.error(buildLogEntry(ex).toLogMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiFailResponse(ex.getMessage()));
    }

    @ExceptionHandler(WaitingException.class)
    public ResponseEntity<ApiFailResponse> handleWaitingException(WaitingException ex) {
        LOGGER.error(buildLogEntry(ex).toLogMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiFailResponse(ex.getMessage()));
    }

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<ApiFailResponse> handlePaymentException(PaymentException ex) {
        LOGGER.error(buildLogEntry(ex).toLogMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiFailResponse(ex.getMessage()));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiFailResponse> handleAuthException(UnauthorizedException ex) {
        LOGGER.error(buildLogEntry(ex).toLogMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiFailResponse("인증에 실패했습니다."));
    }

    @ExceptionHandler(LoginAuthException.class)
    public ResponseEntity<ApiFailResponse> handleLoginAuthException(LoginAuthException ex) {
        LOGGER.error(buildLogEntry(ex).toLogMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiFailResponse("로그인에 실패했습니다. 아이디 또는 비밀번호를 다시 확인하세요"));
    }

    @ExceptionHandler(JwtExtractException.class)
    public ResponseEntity<ApiFailResponse> handleJwtExtractException(JwtExtractException ex) {
        LOGGER.error(buildLogEntry(ex).toLogMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiFailResponse("JWT 추출에 실패했습니다."));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiFailResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        LOGGER.error(buildLogEntry(ex).toLogMessage());
        String errorMessage = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return ResponseEntity.badRequest().body(new ApiFailResponse(errorMessage));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiFailResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        LOGGER.error(buildLogEntry(ex).toLogMessage());
        if (ex.getCause() instanceof InvalidFormatException formatEx && formatEx.getTargetType() == LocalDate.class) {
            return ResponseEntity.badRequest().body(new ApiFailResponse("날짜는 yyyy-MM-dd 형식이어야 합니다."));
        }
        if (ex.getCause() instanceof InvalidFormatException formatEx && formatEx.getTargetType() == LocalTime.class) {
            return ResponseEntity.badRequest().body(new ApiFailResponse("시간은 HH:mm 형식이어야 합니다."));
        }
        return ResponseEntity.badRequest().body(new ApiFailResponse("잘못된 요청입니다."));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiFailResponse> handleException(Exception ex) {
        LOGGER.error(buildLogEntry(ex).toLogMessage());
        return ResponseEntity.internalServerError().body(new ApiFailResponse("예상치 못한 에러가 발생했습니다."));
    }

    private LogEntry buildLogEntry(Throwable ex) {
        StackTraceElement[] stackTrace = ex.getStackTrace();
        if (stackTrace.length == 0) {
            String className = "UnknownClass";
            String methodName = "UnknownMethod";
            return new ErrorLog(className, methodName, ex.getMessage(), ex);
        }
        String className = stackTrace[0].getClassName();
        String methodName = stackTrace[0].getMethodName();
        return new ErrorLog(className, methodName, ex.getMessage(), ex);
    }
}
