package roomescape.payment.infrastructure.toss.exception;

import org.springframework.http.HttpStatus;

public class TossPaymentApprovalFailedException extends TossException {
    public TossPaymentApprovalFailedException(HttpStatus status, String message) {
        super(status, message);
    }
}
