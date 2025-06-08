package roomescape.payment.controller.excpetion;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import roomescape.global.response.ApiResponse;
import roomescape.payment.controller.response.PaymentErrorCode;
import roomescape.payment.exception.PaymentNotFoundException;
import roomescape.payment.exception.PaymentProcessException;
import roomescape.payment.exception.PaymentServerException;

@ControllerAdvice
@Slf4j
public class PaymentExceptionHandler {

    private static final String SERVER_ERROR_CODE = "PF001";
    private static final String PROCESS_ERROR_CODE = "PF002";
    private static final String NOT_FOUND_ERROR_CODE = "PF003";

    @ExceptionHandler(PaymentNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handlePaymentNotFoundException(HttpServletRequest request,
                                                                            PaymentNotFoundException e) {
        log.info("{} | {} {}", request.getAttribute("traceId"), e.getClass(), e.getMessage());
        PaymentErrorCode paymentErrorCode = new PaymentErrorCode(NOT_FOUND_ERROR_CODE, e.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.fail(paymentErrorCode));
    }

    @ExceptionHandler(PaymentServerException.class)
    public ResponseEntity<ApiResponse<Void>> handlePaymentServerException(HttpServletRequest request,
                                                                          PaymentServerException e) {
        log.info("{} | {} {}", request.getAttribute("traceId"), e.getClass(), e.getMessage());
        PaymentErrorCode paymentErrorCode = new PaymentErrorCode(SERVER_ERROR_CODE, e.getMessage());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail(paymentErrorCode));
    }

    @ExceptionHandler(PaymentProcessException.class)
    public ResponseEntity<ApiResponse<Void>> handlePaymentProcessException(HttpServletRequest request,
                                                                           PaymentProcessException e) {
        log.info("{} | {} {}", request.getAttribute("traceId"), e.getClass(), e.getMessage());
        PaymentErrorCode paymentErrorCode = new PaymentErrorCode(PROCESS_ERROR_CODE, e.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(paymentErrorCode));
    }
}
