package roomescape.advice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import roomescape.advice.exception.ExceptionTitle;
import roomescape.advice.exception.RoomEscapeException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final String NULL_POINTER_EXCEPTION_ERROR_MESSAGE = "인자 중 null 값이 존재합니다.";
    private static final String UNEXPECTED_EXCEPTION_ERROR_MESSAGE = "예상치 못한 예외가 발생했습니다. 관리자에게 문의하세요.";
    private static final String DATA_INTEGRITY_VIOLATION_EXCEPTION_ERROR_MESSAGE = "제약 조건에 어긋난 요청입니다.";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(RoomEscapeException.class)
    public ResponseEntity<ProblemDetail> handleRoomEscapeException(RoomEscapeException e) {
        logging(e, HttpStatus.valueOf(e.getStatus()));

        return ResponseEntity.status(e.getStatus())
                .body(e.getProblemDetail());
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ProblemDetail> handleNullPointerException(NullPointerException e) {
        ProblemDetail problemDetail = createProblemDetail(
                NULL_POINTER_EXCEPTION_ERROR_MESSAGE, ExceptionTitle.ILLEGAL_USER_REQUEST);
        logging(e, HttpStatus.valueOf(problemDetail.getStatus()));

        return ResponseEntity.status(problemDetail.getStatus())
                .body(problemDetail);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ProblemDetail> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        ProblemDetail problemDetail = createProblemDetail(
                DATA_INTEGRITY_VIOLATION_EXCEPTION_ERROR_MESSAGE, ExceptionTitle.ILLEGAL_USER_REQUEST);
        logging(e, HttpStatus.valueOf(problemDetail.getStatus()));

        return ResponseEntity.status(problemDetail.getStatus())
                .body(problemDetail);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleUnexpectedException(Exception e) {
        ProblemDetail problemDetail = createProblemDetail(
                UNEXPECTED_EXCEPTION_ERROR_MESSAGE, ExceptionTitle.INTERNAL_SERVER_ERROR);
        logging(e, HttpStatus.valueOf(problemDetail.getStatus()));

        return ResponseEntity.status(problemDetail.getStatus())
                .body(problemDetail);
    }

    private ProblemDetail createProblemDetail(String message, ExceptionTitle title) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(title.getStatusCode(), message);
        problemDetail.setTitle(title.getTitle());

        return problemDetail;
    }

    private void logging(Exception e, HttpStatus status) {
        if (status.is5xxServerError()) {
            logger.error(e.getMessage(), e);
            return;
        }
        logger.warn(e.getMessage(), e);
    }
}
