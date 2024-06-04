package roomescape.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

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

@ControllerAdvice
public class RoomescapeExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(RoomescapeExceptionHandler.class);

    @ExceptionHandler(RoomescapeException.class)
    public ErrorResponse handle(RoomescapeException e) {
        logger.error("LogMessage : {}, Message : {}", e.getLogMessage(), e.getMessage());
        return ErrorResponse.builder(e, BAD_REQUEST, e.getMessage())
                .build();
    }

    @ExceptionHandler(UnAuthorizedException.class)
    public ErrorResponse handle(UnAuthorizedException e) {
        logger.error("LogMessage : {}, Message : {}", e.getLogMessage(), e.getMessage());
        return ErrorResponse.builder(e, BAD_REQUEST, e.getMessage())
                .build();
    }

    @ExceptionHandler(ForbiddenException.class)
    public ErrorResponse handle(ForbiddenException e) {
        logger.error("LogMessage : {}, Message : {}", e.getLogMessage(), e.getMessage());
        return ErrorResponse.builder(e, BAD_REQUEST, e.getMessage())
                .build();
    }

    @ExceptionHandler(PaymentException.class)
    public ErrorResponse handle(PaymentException e) {
        logger.error("ErrorCode : {}, ForUserMessage : {}",
                e.getUserPaymentExceptionResponse().getErrorCode(), e.getMessage());
        return ErrorResponse.builder(e, BAD_REQUEST, e.getMessage())
                .build();
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ErrorResponse handle(HttpMessageNotReadableException e) {
        logger.error("LogMessage : {}, Message : {}", INVALID_DATE_TIME_FORMAT.getLogMessageFormat(), e.getMessage());
        return ErrorResponse.builder(e, BAD_REQUEST, INVALID_DATE_TIME_FORMAT.getMessage())
                .build();
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ErrorResponse handle(MissingServletRequestParameterException e) {
        logger.error("LogMessage : {}, Message : {}", NO_QUERY_PARAMETER.getLogMessageFormat(), e.getMessage());
        return ErrorResponse.builder(e, BAD_REQUEST, NO_QUERY_PARAMETER.getMessage())
                .build();
    }

    @ExceptionHandler(Exception.class)
    public ErrorResponse handle(Exception e) {
        logger.error("LogMessage : {}, Message : {}", UN_EXPECTED_ERROR.getLogMessageFormat(), e.getMessage());
        return ErrorResponse.builder(e, INTERNAL_SERVER_ERROR, UN_EXPECTED_ERROR.getMessage())
                .build();
    }
}
