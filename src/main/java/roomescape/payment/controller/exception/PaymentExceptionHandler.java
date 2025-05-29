package roomescape.payment.controller.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import roomescape.global.response.ApiResponse;
import roomescape.payment.controller.response.PaymentErrorCode;
import roomescape.payment.exception.PaymentProcessException;
import roomescape.payment.exception.PaymentServerException;

@ControllerAdvice
public class PaymentExceptionHandler {

    private static final String SERVER_ERROR_CODE = "PF001";
    private static final String PROCESS_ERROR_CODE = "PF002";

    @ExceptionHandler(PaymentServerException.class)
    public ResponseEntity<ApiResponse<Void>> handlePaymentServerException(PaymentServerException e) {
        PaymentErrorCode paymentErrorCode = new PaymentErrorCode(SERVER_ERROR_CODE, e.getMessage());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail(paymentErrorCode));
    }

    @ExceptionHandler(PaymentProcessException.class)
    public ResponseEntity<ApiResponse<Void>> handlePaymentProcessException(PaymentProcessException e) {
        PaymentErrorCode paymentErrorCode = new PaymentErrorCode(PROCESS_ERROR_CODE, e.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(paymentErrorCode));
    }
}
