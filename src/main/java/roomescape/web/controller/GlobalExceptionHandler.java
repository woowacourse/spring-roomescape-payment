package roomescape.web.controller;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import roomescape.dto.payment.TossError;
import roomescape.exception.TossClientException;
import roomescape.exception.TossServerException;

@ControllerAdvice
class GlobalExceptionHandler {

    private final Logger logger = Logger.getLogger(getClass().getName());

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    private ProblemDetail handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        String logMessage = """
                [%s]
                messgage : %s
                """.formatted(e.getClass().getName(), e.getMessage());
        logger.log(Level.SEVERE, logMessage);

        String errorMessage = "요청 본문을 읽을 수 없습니다. 요청 형식을 확인해 주세요.";
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, errorMessage);
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    private ProblemDetail handleIllegalArgumentException(IllegalArgumentException e) {
        Throwable cause = e.getCause();

        if (cause != null) {
            String logMessage = """
                    [%s]
                    message : %s
                    detail : %s
                    """.formatted(e.getClass().getName(), e.getMessage(), cause.getMessage());
            logger.log(Level.SEVERE, logMessage);
        }

        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(value = TossClientException.class)
    private ProblemDetail handleTossClientException(TossClientException e) {
        TossError tossError = e.getTossError();

        String logMessage = """
                [%s]
                code : %s
                message : %s
                """.formatted(e.getClass().getName(), tossError.code(), tossError.message());
        logger.log(Level.SEVERE, logMessage);

        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, tossError.message());
    }

    @ExceptionHandler(value = TossServerException.class)
    private ProblemDetail handleTossServerException(TossServerException e) {
        TossError tossError = e.getTossError();

        String logMessage = """
                [%s]
                code : %s
                message : %s
                """.formatted(e.getClass().getName(), tossError.code(), tossError.message());
        logger.log(Level.SEVERE, logMessage);

        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, tossError.message());
    }

    @ExceptionHandler(value = Exception.class)
    private ProblemDetail handleGeneralException(Exception e) {
        String logMessage = """
                [%s]
                message : %s
                """.formatted(e.getClass().getName(), e.getMessage());
        logger.log(Level.SEVERE, logMessage);
        String errorMessage = "시스템에서 오류가 발생했습니다. 관리자에게 문의해주세요.";
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage);
    }
}
