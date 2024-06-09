package roomescape.web.controller.exception;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import roomescape.dto.payment.TossError;
import roomescape.exception.ErrorCode;
import roomescape.exception.ExternalApiTimeoutException;
import roomescape.exception.RoomEscapeException;
import roomescape.exception.TossClientException;
import roomescape.exception.TossServerException;

@ControllerAdvice
class GlobalExceptionHandler {

    private static final String LOG_MESSAGE = """
                [%s]
                message : %s
            """;

    private static final String LOG_MESSAGE_WITH_DETAIL = """
                [%s]
                message : %s
                detail : %s
            """;

    private static final String LOG_MESSAGE_WITH_CODE = """
                [%s]
                code : %s
                message : %s
            """;

    private final Logger logger;

    private GlobalExceptionHandler() {
        this.logger = Logger.getLogger(getClass().getName());
    }

    @ExceptionHandler(value = TossClientException.class)
    private ProblemDetail handleTossClientException(TossClientException e) {
        TossError tossError = e.getTossError();

        String logMessage = LOG_MESSAGE_WITH_CODE.formatted(
                e.getClass().getName(),
                tossError.code(),
                tossError.message()
        );
        logger.log(Level.SEVERE, logMessage);

        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, tossError.message());
    }

    @ExceptionHandler(value = TossServerException.class)
    private ProblemDetail handleTossServerException(TossServerException e) {
        TossError tossError = e.getTossError();

        String logMessage = LOG_MESSAGE_WITH_CODE.formatted(
                e.getClass().getName(),
                tossError.code(),
                tossError.message()
        );
        logger.log(Level.SEVERE, logMessage);

        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, tossError.message());
    }

    @ExceptionHandler(value = RoomEscapeException.class)
    private ProblemDetail handleRoomEscapeException(RoomEscapeException e) {
        ErrorCode errorCode = e.getErrorCode();

        String logMessage = LOG_MESSAGE_WITH_DETAIL.formatted(
                e.getClass().getName(),
                errorCode.getMessage(),
                e.getDetail()
        );
        logger.log(Level.SEVERE, logMessage);

        return ProblemDetail.forStatusAndDetail(errorCode.getHttpStatus(), errorCode.getMessage());
    }

    @ExceptionHandler(value = ExternalApiTimeoutException.class)
    private ProblemDetail handleExternalApiTimeoutException(ExternalApiTimeoutException e) {
        String logMessage = LOG_MESSAGE.formatted(e.getClass().getName(), e.getMessage());
        logger.log(Level.SEVERE, logMessage);

        ErrorCode errorCode = ErrorCode.EXTERNAL_API_TIMEOUT;
        return ProblemDetail.forStatusAndDetail(errorCode.getHttpStatus(), errorCode.getMessage());
    }

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    private ProblemDetail handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        String logMessage = LOG_MESSAGE.formatted(e.getClass().getName(), e.getMessage());
        logger.log(Level.SEVERE, logMessage);

        ErrorCode errorCode = ErrorCode.HTTP_BODY_NOT_READABLE;
        return ProblemDetail.forStatusAndDetail(errorCode.getHttpStatus(), errorCode.getMessage());
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    private ProblemDetail handleIllegalArgumentException(IllegalArgumentException e) {
        String logMessage = LOG_MESSAGE.formatted(e.getClass().getName(), e.getMessage());
        logger.log(Level.SEVERE, logMessage);

        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        return ProblemDetail.forStatusAndDetail(errorCode.getHttpStatus(), errorCode.getMessage());
    }

    @ExceptionHandler(value = Exception.class)
    private ProblemDetail handleGeneralException(Exception e) {
        String logMessage = LOG_MESSAGE.formatted(e.getClass().getName(), e.getMessage());
        logger.log(Level.SEVERE, logMessage);

        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        return ProblemDetail.forStatusAndDetail(errorCode.getHttpStatus(), errorCode.getMessage());
    }
}
