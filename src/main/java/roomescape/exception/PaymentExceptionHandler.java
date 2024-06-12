package roomescape.exception;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import roomescape.controller.PaymentController;
import roomescape.dto.ErrorResponse;

@ControllerAdvice(assignableTypes = PaymentController.class) // PaymentController의 메소드만 이 예외를 잡고싶어 =>
public class PaymentExceptionHandler {

    private static final Set<String> SERVER_ERROR_CODES = Set.of(
            "INVALID_API_KEY",
            "NOT_FOUND_TERMINAL_ID",
            "INVALID_AUTHORIZE_AUTH",
            "INVALID_UNREGISTERED_SUBMALL",
            "UNAPPROVED_ORDER_ID",
            "UNAUTHORIZED_KEY",
            "FORBIDDEN_REQUEST",
            "INCORRECT_BASIC_AUTH_FORMAT",
            "FAILED_PAYMENT_INTERNAL_SYSTEM_PROCESSING",
            "FAILED_INTERNAL_SYSTEM_PROCESSING",
            "UNKNOWN_PAYMENT_ERROR"
    );

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final StringWriter stringWriter = new StringWriter();
    private final PrintWriter printWriter = new PrintWriter(stringWriter);

    @ExceptionHandler(PaymentException.class)
    @ApiResponse(responseCode = "400", description = "잘못된 결제 정보", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "결제 서버 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<ErrorResponse> handle(PaymentException e) {
        logger.error(getStackTrace(e));
        if (SERVER_ERROR_CODES.contains(e.getCode())) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(e.getMessage()));
        }
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(e.getMessage()));
    }

    private String getStackTrace(Exception e) {
        e.printStackTrace(printWriter);
        return stringWriter.toString();
    }
}
