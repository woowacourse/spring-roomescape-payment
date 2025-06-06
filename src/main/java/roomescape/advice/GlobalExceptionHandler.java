package roomescape.advice;

import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import roomescape.aop.LoggingAspect;
import roomescape.exception.BadRequestException;
import roomescape.exception.ExternalApiConnectionException;
import roomescape.exception.ForbiddenException;
import roomescape.exception.InternalServerException;
import roomescape.exception.LoginFailException;
import roomescape.exception.NotFoundException;
import roomescape.exception.PaymentException;
import roomescape.exception.UnauthorizedException;


@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ProblemDetail> notFoundExceptionHandler(NotFoundException exception) {
        log.warn("[WARN][{}]", LocalDateTime.now(), exception);
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problemDetail.setTitle("데이터가 존재하지 않습니다.");
        problemDetail.setDetail(exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ProblemDetail> badRequestExceptionHandler(BadRequestException exception) {
        log.warn("[WARN][{}]", LocalDateTime.now(), exception);
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("올바르지 않은 입력입니다.");
        problemDetail.setDetail(exception.getMessage());
        return ResponseEntity.badRequest().body(problemDetail);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ProblemDetail> unauthorizedExceptionHandler(UnauthorizedException exception) {
        log.warn("[WARN][{}]", LocalDateTime.now(), exception);
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        problemDetail.setTitle("인증을 먼저 진행해주세요.");
        problemDetail.setDetail(exception.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problemDetail);
    }

    @ExceptionHandler(LoginFailException.class)
    public ResponseEntity<ProblemDetail> loginFailExceptionHandler(LoginFailException exception) {
        log.warn("[WARN][{}]", LocalDateTime.now(), exception);
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("로그인에 실패했습니다.");
        problemDetail.setDetail("로그인 정보를 다시 확인해주세요.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ProblemDetail> forbiddenExceptionHandler(ForbiddenException exception) {
        log.warn("[WARN][{}]", LocalDateTime.now(), exception);
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        problemDetail.setTitle("권한이 없습니다.");
        problemDetail.setDetail(exception.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(problemDetail);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> methodArgumentNotValidExceptionHandler(
            MethodArgumentNotValidException exception
    ) {
        log.warn("[WARN][{}]", LocalDateTime.now(), exception);
        List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
        List<String> messages = fieldErrors.stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("올바르지 않은 입력입니다.");
        problemDetail.setDetail(String.join("\n", messages));
        return ResponseEntity.badRequest().body(problemDetail);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ProblemDetail> handlerMethodValidationExceptionHandler(
            HandlerMethodValidationException exception
    ) {
        log.warn("[WARN][{}]", LocalDateTime.now(), exception);
        List<String> errorMessage = exception.getAllErrors().stream()
                .map(MessageSourceResolvable::getDefaultMessage)
                .toList();
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("올바르지 않은 입력입니다.");
        problemDetail.setDetail(String.join("\n", errorMessage));
        return ResponseEntity.badRequest().body(problemDetail);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetail> httpMessageNotReadableExceptionHandler(
            HttpMessageNotReadableException exception
    ) {
        log.warn("[WARN][{}]", LocalDateTime.now(), exception);
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("올바르지 않은 입력입니다.");
        problemDetail.setDetail("요청 메세지의 형식을 다시 확인해주세요.");
        return ResponseEntity.badRequest().body(problemDetail);
    }

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<ProblemDetail> paymentExceptionHandler(
            PaymentException exception
    ) {
        log.warn("[WARN][{}]", LocalDateTime.now(), exception);
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("결제 승인에 실패했습니다");
        problemDetail.setDetail(exception.getMessage());
        return ResponseEntity.badRequest().body(problemDetail);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> illegalArgumentExceptionHandler(IllegalArgumentException exception) {
        log.warn("[WARN][{}]", LocalDateTime.now(), exception);
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("올바르지 않은 입력입니다.");
        problemDetail.setDetail(exception.getMessage());
        return ResponseEntity.badRequest().body(problemDetail);
    }

    @ExceptionHandler(ExternalApiConnectionException.class)
    public ResponseEntity<ProblemDetail> externalApiConnectionExceptionHandler(
            ExternalApiConnectionException exception
    ) {
        log.warn("[WARN][{}]", LocalDateTime.now(), exception);
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("외부 API 연결에 실패했습니다.");
        problemDetail.setDetail(exception.getMessage());
        return ResponseEntity.badRequest().body(problemDetail);
    }

    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity<ProblemDetail> internalServerExceptionHandler(InternalServerException exception) {
        log.error("[ERROR][{}]", LocalDateTime.now(), exception);
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problemDetail.setTitle("서버 내부 에러입니다.");
        problemDetail.setDetail("서버 내부에서 로직 예외 발생했습니다.");
        return ResponseEntity.internalServerError().body(problemDetail);
    }
}
