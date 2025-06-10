package roomescape.payment.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import roomescape.payment.infra.toss.dto.TossPaymentErrorResponse;

@Getter
public class PaymentProcessException extends RuntimeException {
    private final HttpStatus status;

    public PaymentProcessException(TossPaymentErrorResponse tossPaymentErrorResponse) {
        super(tossPaymentErrorResponse.message());
        this.status = parseStatus(tossPaymentErrorResponse.code());
    }

    private HttpStatus parseStatus(String code) {
        try {
            int parseCode = Integer.parseInt(code);
            return parseStatus(parseCode);
        } catch (NumberFormatException e) {
            return HttpStatus.BAD_REQUEST;
        }
    }

    private HttpStatus parseStatus(int code) {
        HttpStatus status = HttpStatus.resolve(code);
        if (status == null) {
            return HttpStatus.BAD_REQUEST;
        }
        return status;
    }
}
