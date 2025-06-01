package roomescape.payment.controller.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import roomescape.global.response.ApiResponse;
import roomescape.payment.controller.response.TossPaymentErrorCode;
import roomescape.payment.exception.PaymentProcessException;
import roomescape.payment.exception.PaymentServerException;

@ControllerAdvice
public class PaymentExceptionHandler {

    private static final String SERVER_ERROR_CODE = "PF001";
    private static final String PROCESS_ERROR_CODE = "PF002";

    @ExceptionHandler(PaymentServerException.class)
    public ResponseEntity<ApiResponse<Void>> handlePaymentServerException(PaymentServerException e) {
        TossPaymentErrorCode tossPaymentErrorCode = new TossPaymentErrorCode(SERVER_ERROR_CODE, e.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(ApiResponse.fail(tossPaymentErrorCode));
    }

    @ExceptionHandler(PaymentProcessException.class)
    public ResponseEntity<ApiResponse<Void>> handlePaymentProcessException(PaymentProcessException e) {
        TossPaymentErrorCode tossPaymentErrorCode = new TossPaymentErrorCode(PROCESS_ERROR_CODE, e.getMessage());

        return ResponseEntity
                .status(e.getStatus())
                .body(ApiResponse.fail(tossPaymentErrorCode));
    }
}
