package roomescape.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import static roomescape.exception.type.RoomescapeExceptionType.INVALID_DATE_TIME_FORMAT;
import static roomescape.exception.type.RoomescapeExceptionType.NO_QUERY_PARAMETER;
import static roomescape.exception.type.RoomescapeExceptionType.UN_EXPECTED_ERROR;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@ControllerAdvice
public class RoomescapeExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(RoomescapeExceptionHandler.class);

    @ExceptionHandler(RoomescapeException.class)
    @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ErrorResponse handle(RoomescapeException e) {
        log.error("LogMessage : {}, Message : {}", e.getLogMessage(), e.getMessage());
        return ErrorResponse.builder(e, BAD_REQUEST, e.getMessage())
                .build();
    }

    @ExceptionHandler(UnAuthorizedException.class)
    @ApiResponse(responseCode = "401", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ErrorResponse handle(UnAuthorizedException e) {
        log.error("LogMessage : {}, Message : {}", e.getLogMessage(), e.getMessage());
        return ErrorResponse.builder(e, UNAUTHORIZED, e.getMessage())
                .build();
    }

    @ExceptionHandler(ForbiddenException.class)
    @ApiResponse(responseCode = "403", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ErrorResponse handle(ForbiddenException e) {
        log.error("LogMessage : {}, Message : {}", e.getLogMessage(), e.getMessage());
        return ErrorResponse.builder(e, FORBIDDEN, e.getMessage())
                .build();
    }

    @ExceptionHandler(PaymentException.class)
    @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ErrorResponse handle(PaymentException e) {
        log.error("ErrorCode : {}, ForUserMessage : {}",
                e.getUserPaymentExceptionResponse().getErrorCode(), e.getMessage());
        return ErrorResponse.builder(e, BAD_REQUEST, e.getMessage())
                .build();
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ErrorResponse handle(HttpMessageNotReadableException e) {
        log.error("LogMessage : {}, Message : {}", INVALID_DATE_TIME_FORMAT.getLogMessageFormat(), e.getMessage());
        return ErrorResponse.builder(e, BAD_REQUEST, INVALID_DATE_TIME_FORMAT.getMessage())
                .build();
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ErrorResponse handle(MissingServletRequestParameterException e) {
        log.error("LogMessage : {}, Message : {}", NO_QUERY_PARAMETER.getLogMessageFormat(), e.getMessage());
        return ErrorResponse.builder(e, BAD_REQUEST, NO_QUERY_PARAMETER.getMessage())
                .build();
    }

    @ExceptionHandler(Exception.class)
    @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ErrorResponse handle(Exception e) {
        log.error("LogMessage : {}, Message : {}", UN_EXPECTED_ERROR.getLogMessageFormat(), e.getMessage());
        return ErrorResponse.builder(e, INTERNAL_SERVER_ERROR, UN_EXPECTED_ERROR.getMessage())
                .build();
    }
}
