package roomescape.domain.payment;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public class PaymentStatus {

    private final PaymentStatusCode code;
    private final String message;

    private PaymentStatus(final PaymentStatusCode code, final String message) {
        this.code = code;
        this.message = message;
    }

    public boolean causedBy(final PaymentStatusCode.Cause cause) {
        return code.causedBy(cause);
    }

    public static PaymentStatus fail(final PaymentStatusCode code, final String message) {
        return new PaymentStatus(code, message);
    }
}
