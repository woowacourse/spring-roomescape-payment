package roomescape.payment.exception;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PaymentServerException extends RuntimeException {
    public PaymentServerException(String message) {
        super(message);
    }
}
