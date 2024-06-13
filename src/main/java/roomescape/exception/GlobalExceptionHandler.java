package roomescape.exception;

import static roomescape.exception.ExceptionType.INVALID_DATE_TIME_FORMAT;
import static roomescape.exception.ExceptionType.UNEXPECTED_ERROR;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import roomescape.dto.ErrorResponse;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final StringWriter stringWriter = new StringWriter();
    private final PrintWriter printWriter = new PrintWriter(stringWriter);

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<ErrorResponse> handle(HttpMessageNotReadableException e) {
        logger.error(getStackTrace(e));
        return ResponseEntity.status(INVALID_DATE_TIME_FORMAT.getStatus())
                .body(new ErrorResponse(INVALID_DATE_TIME_FORMAT.getMessage()));
    }

    @ExceptionHandler(RoomescapeException.class)
    @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "401", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "403", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<ErrorResponse> handle(RoomescapeException e) {
        logger.error(getStackTrace(e));
        return ResponseEntity
                .status(e.getHttpStatus())
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<ErrorResponse> handle(Exception e) {
        logger.error(getStackTrace(e));
        return ResponseEntity
                .status(UNEXPECTED_ERROR.getStatus())
                .body(new ErrorResponse(UNEXPECTED_ERROR.getMessage()));
    }

    private String getStackTrace(Exception e) {
        e.printStackTrace(printWriter);
        return stringWriter.toString();
    }
}
