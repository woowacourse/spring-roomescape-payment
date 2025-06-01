package roomescape.payment.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import roomescape.payment.toss.dto.TossPaymentErrorResponse;

@Getter
public class PaymentProcessException extends RuntimeException {
    private final HttpStatus status;

    public PaymentProcessException(TossPaymentErrorResponse tossPaymentErrorResponse) {
        super(tossPaymentErrorResponse.message());
        this.status = parseStatus(tossPaymentErrorResponse.code());
    }

    private HttpStatus parseStatus(String code) {
        try {
            return HttpStatus.resolve(Integer.parseInt(code));
        } catch (NumberFormatException e) {
            return HttpStatus.BAD_REQUEST;
        }
    }
}
