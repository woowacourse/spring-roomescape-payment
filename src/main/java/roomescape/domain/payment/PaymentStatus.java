package roomescape.domain.payment;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public class PaymentStatus {

    private static final String SUCCESS_CODE = "success";
    private static final String SUCCESS_MESSAGE = "결제에 성공했습니다.";

    private final String code;
    private final String message;

    private PaymentStatus(final String code, final String message) {
        this.code = code;
        this.message = message;
    }

    public boolean isFailed() {
        return !SUCCESS_CODE.equals(code);
    }

    public static PaymentStatus succeed() {
        return new PaymentStatus(SUCCESS_CODE, SUCCESS_MESSAGE);
    }

    public static PaymentStatus fail(final String code, final String message) {
        return new PaymentStatus(code, message);
    }
}
