package roomescape.domain.payment;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public class TransactionStatus {

    private static final String SUCCESS_MESSAGE = "결제에 성공했습니다.";

    private final TransactionStatusCode code;
    private final String message;

    private TransactionStatus(final TransactionStatusCode code, final String message) {
        this.code = code;
        this.message = message;
    }

    public boolean isFailed() {
        return TransactionStatusCode.SUCCEEDED_PAYMENT != code;
    }

    public static TransactionStatus succeed() {
        return new TransactionStatus(TransactionStatusCode.SUCCEEDED_PAYMENT, SUCCESS_MESSAGE);
    }

    public static TransactionStatus fail(final TransactionStatusCode code, final String message) {
        return new TransactionStatus(code, message);
    }
}
