package roomescape.domain.payment;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public class PaymentStatus {

    private static final String SUCCESS_MESSAGE = "결제에 성공했습니다.";

    private final PaymentStatusCode code;
    private final String message;

    private PaymentStatus(final PaymentStatusCode code, final String message) {
        this.code = code;
        this.message = message;
    }

    public boolean isFailed() {
        return PaymentStatusCode.SUCCEEDED_PAYMENT != code;
    }

    public static PaymentStatus succeed() {
        return new PaymentStatus(PaymentStatusCode.SUCCEEDED_PAYMENT, SUCCESS_MESSAGE);
    }

    public static PaymentStatus fail(final PaymentStatusCode code, final String message) {
        return new PaymentStatus(code, message);
    }
}
