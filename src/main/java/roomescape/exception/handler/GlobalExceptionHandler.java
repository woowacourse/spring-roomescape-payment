package roomescape.exception.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;
import roomescape.client.PaymentErrorResponse;
import roomescape.exception.*;
import roomescape.exception.dto.ApiErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleBadRequestException(BadRequestException ex) {
        log.error("예외 발생: ", ex);
        return new ApiErrorResponse(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiErrorResponse handleNotFoundException(NotFoundException ex) {
        log.error("예외 발생: ", ex);
        return new ApiErrorResponse(
                ex.getMessage(),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(UnauthorizationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiErrorResponse handleAuthorizationException(UnauthorizationException ex) {
        log.error("예외 발생: ", ex);
        return new ApiErrorResponse(
                ex.getMessage(),
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiErrorResponse handleAccessDeniedException(AccessDeniedException ex) {
        log.error("예외 발생: ", ex);
        return new ApiErrorResponse(
                ex.getMessage(),
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(PaymentConfirmClientException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handlePaymentConfirmClientException(PaymentConfirmClientException ex) {
        PaymentErrorResponse paymentErrorResponse = ex.getPaymentErrorResponse();
        String errorMessage = paymentErrorResponse.getMessage();
        log.error("예외 발생: ", ex);
        return new ApiErrorResponse(
                errorMessage,
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(PaymentConfirmServerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiErrorResponse handlePaymentConfirmServerException(PaymentConfirmServerException ex) {
        log.error("예외 발생: ", ex);
        return new ApiErrorResponse(
                "결제가 실패했습니다. 잠시 후 다시 시도해주세요.",
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(ResourceAccessException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    public ApiErrorResponse handleResourceAccessException(ResourceAccessException ex) {
        log.error("예외 발생: ", ex);
        return new ApiErrorResponse("외부 서비스에 문제가 발생했습니다.", HttpStatus.BAD_GATEWAY);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiErrorResponse handleDefault(Exception ex) {
        log.error("예외 발생: ", ex);
        return new ApiErrorResponse(
                ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
