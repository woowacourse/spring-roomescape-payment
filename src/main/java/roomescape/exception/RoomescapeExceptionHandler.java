package roomescape.exception;

import static roomescape.exception.type.RoomescapeExceptionType.INVALID_DATE_TIME_FORMAT;
import static roomescape.exception.type.RoomescapeExceptionType.NO_QUERY_PARAMETER;
import static roomescape.exception.type.RoomescapeExceptionType.UN_EXPECTED_ERROR;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import roomescape.exception.response.PaymentExceptionResponse;

@ControllerAdvice
public class RoomescapeExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(RoomescapeExceptionHandler.class);

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ErrorResponse handle(HttpMessageNotReadableException e) {
        logger.error("LogMessage : {}, Message : {}", INVALID_DATE_TIME_FORMAT.getLogMessage(), e.getMessage());
        return ErrorResponse.builder(e, INVALID_DATE_TIME_FORMAT.getStatus(), INVALID_DATE_TIME_FORMAT.getMessage())
                .build();
    }

    @ExceptionHandler(RoomescapeException.class)
    public ErrorResponse handle(RoomescapeException e) {
        logger.error("LogMessage : {}, Message : {}", e.getLogMessage(), e.getMessage());
        return ErrorResponse.builder(e, e.getExceptionType().getStatus(), e.getMessage())
                .build();
    }

    @ExceptionHandler(PaymentException.class)
    public ErrorResponse handle(PaymentException e) {
        PaymentExceptionResponse paymentExceptionResponse = e.getPaymentExceptionResponse();
        logger.error("ErrorCode : {}, Message : {}", paymentExceptionResponse.getErrorCode(), paymentExceptionResponse.getMessage());
        return ErrorResponse.builder(e, paymentExceptionResponse.getHttpStatusCode(), paymentExceptionResponse.getMessage())
                .build();
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ErrorResponse handle(MissingServletRequestParameterException e) {
        logger.error("LogMessage : {}, Message : {}", NO_QUERY_PARAMETER.getLogMessage(), e.getMessage());
        return ErrorResponse.builder(e, NO_QUERY_PARAMETER.getStatus(), NO_QUERY_PARAMETER.getMessage())
                .build();
    }

    @ExceptionHandler(Exception.class)
    public ErrorResponse handle(Exception e) {
        logger.error("LogMessage : {}, Message : {}", UN_EXPECTED_ERROR.getLogMessage(), e.getMessage());
        return ErrorResponse.builder(e, UN_EXPECTED_ERROR.getStatus(), UN_EXPECTED_ERROR.getMessage())
                .build();
    }
}
