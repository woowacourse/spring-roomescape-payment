package roomescape.common;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonMappingException.Reference;
import io.jsonwebtoken.JwtException;
import java.net.SocketTimeoutException;
import java.util.NoSuchElementException;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import roomescape.common.exception.ForbiddenException;
import roomescape.common.exception.UnAuthorizationException;
import roomescape.payment.client.toss.TossClientErrorResponse;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = Logger.getLogger("Logger");
    private static final String EXCEPTION_PREFIX = "[ERROR] ";

    @ExceptionHandler
    public ResponseEntity<ProblemDetail> catchValidationException(MethodArgumentNotValidException ex) {
        String exceptionMessages = ex.getBindingResult().getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining("\n"));

        logger.warning(EXCEPTION_PREFIX + exceptionMessages);
        return ResponseEntity.status(400)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exceptionMessages));
    }

    @ExceptionHandler
    public ResponseEntity<ProblemDetail> catchHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        logger.warning(EXCEPTION_PREFIX + ex.getClass() + " " + ex.getMessage());
        String exceptionMessage = "잘못된 JSON 형식입니다.";
        if (ex.getCause() instanceof JsonMappingException jsonMappingException) {
            exceptionMessage = jsonMappingException.getPath().stream()
                    .map(Reference::getFieldName)
                    .collect(Collectors.joining(" ")) + " 필드의 형식이 잘못되었습니다.";
        }

        return ResponseEntity.status(400)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exceptionMessage));
    }

    @ExceptionHandler
    public ResponseEntity<ProblemDetail> catchHttpClientErrorException(HttpClientErrorException ex) {
        logger.warning(EXCEPTION_PREFIX + ex.getMessage());

        TossClientErrorResponse response = ex.getResponseBodyAs(TossClientErrorResponse.class);
        return ResponseEntity.status(ex.getStatusCode())
                .body(ProblemDetail.forStatusAndDetail(ex.getStatusCode(), response.message()));
    }

    @ExceptionHandler
    public ResponseEntity<ProblemDetail> catchHttpServerErrorException(HttpServerErrorException ex) {
        logger.warning(EXCEPTION_PREFIX + ex.getMessage());

        TossClientErrorResponse response = ex.getResponseBodyAs(TossClientErrorResponse.class);
        return ResponseEntity.status(ex.getStatusCode())
                .body(ProblemDetail.forStatusAndDetail(ex.getStatusCode(), response.message()));
    }

    @ExceptionHandler
    public ResponseEntity<ProblemDetail> catchBadRequestException(IllegalArgumentException ex) {
        logger.warning(EXCEPTION_PREFIX + ex.getMessage());
        return ResponseEntity.status(400)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage()));
    }

    @ExceptionHandler({
            UnAuthorizationException.class,
            JwtException.class
    })
    public ResponseEntity<ProblemDetail> catchUnauthorizedException(Exception ex) {
        logger.warning(EXCEPTION_PREFIX + ex.getMessage());
        return ResponseEntity.status(401)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ProblemDetail> catchForbiddenException(ForbiddenException ex) {
        logger.warning(EXCEPTION_PREFIX + ex.getMessage());
        return ResponseEntity.status(403)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage()));
    }

    @ExceptionHandler({
            InvalidDataAccessApiUsageException.class,
            NoSuchElementException.class
    })
    public ResponseEntity<ProblemDetail> catchNotFoundException(RuntimeException ex) {
        logger.warning(EXCEPTION_PREFIX + ex.getMessage());
        return ResponseEntity.status(404)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ProblemDetail> catchConflictException(IllegalStateException ex) {
        logger.warning(EXCEPTION_PREFIX + ex.getMessage());
        return ResponseEntity.status(409)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage()));
    }

    @ExceptionHandler({
            SocketTimeoutException.class,
            ResourceAccessException.class
    })
    public ResponseEntity<ProblemDetail> catchSocketTimeoutException(SocketTimeoutException ex) {
        logger.warning(EXCEPTION_PREFIX + ex.getClass() + " " + ex.getMessage());
        String exceptionMessage = "요청 처리 중 처리 중단(Timeout)이 발생했습니다. 잠시 후 다시 시도해 주세요.";

        return ResponseEntity.status(500)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, exceptionMessage));
    }

    @ExceptionHandler
    public ResponseEntity<ProblemDetail> catchInternalServerException(Exception ex) {
        logger.warning(EXCEPTION_PREFIX + ex.getClass() + " " + ex.getMessage());
        return ResponseEntity.status(500)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage()));
    }
}
