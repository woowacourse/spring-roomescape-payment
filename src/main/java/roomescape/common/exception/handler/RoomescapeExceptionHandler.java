package roomescape.common.exception.handler;

import java.util.zip.DataFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import roomescape.common.exception.DuplicatedException;
import roomescape.common.exception.NotFoundException;
import roomescape.common.exception.OperationNotAllowedException;
import roomescape.common.exception.PaymentClientException;
import roomescape.common.exception.PaymentServerException;
import roomescape.common.exception.ResourceInUseException;
import roomescape.common.exception.UnauthorizedException;

@RestControllerAdvice
public class RoomescapeExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(RoomescapeExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleUnDefinedException(Exception ex) {
        logger.error(ex.getMessage(), ex);
        return "서버 내부에서 알 수 없는 문제가 발생했습니다.";
    }

    @ExceptionHandler(ResourceAccessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleResourceAccessException(Exception ex) {
        logger.error(ex.getMessage(), ex);
        return "외부 요청 시간을 초과했습니다.";
    }

    @ExceptionHandler(PaymentServerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handlePaymentServerException(Exception ex) {
        logger.error(ex.getMessage(), ex);
        return "결제 승인 중 서버 내부에서 알 수 없는 문제가 발생했습니다.";
    }

    @ExceptionHandler(PaymentClientException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handlePaymentClientException(Exception ex) {
        logger.error(ex.getMessage(), ex);
        return ex.getMessage();
    }

    @ExceptionHandler({IllegalStateException.class, DataFormatException.class,
        IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleException(Exception ex) {
        logger.error(ex.getMessage(), ex);
        return ex.getMessage();
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleException(NotFoundException ex) {
        logger.error(ex.getMessage(), ex);
        return ex.getMessage();
    }

    @ExceptionHandler(DuplicatedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleException(DuplicatedException ex) {
        logger.error(ex.getMessage(), ex);
        return ex.getMessage();
    }

    @ExceptionHandler(ResourceInUseException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public String handleException(ResourceInUseException ex) {
        logger.error(ex.getMessage(), ex);
        return ex.getMessage();
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String handleException(UnauthorizedException ex) {
        logger.error(ex.getMessage(), ex);
        return ex.getMessage();
    }

    @ExceptionHandler(OperationNotAllowedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleException(OperationNotAllowedException ex) {
        logger.error(ex.getMessage(), ex);
        return ex.getMessage();
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNoResourceFound(NoResourceFoundException ex) {
        return "";
    }
}
