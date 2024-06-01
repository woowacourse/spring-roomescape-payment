package roomescape.infrastructure.payment.response;

import java.util.Arrays;
import java.util.Optional;

public enum PaymentServerErrorCode {

    INVALID_API_KEY,
    INVALID_AUTHORIZE_AUTH,
    NOT_REGISTERED_BUSINESS,
    UNAUTHORIZED_KEY,
    INCORRECT_BASIC_AUTH_FORMAT,
    FAILED_PAYMENT_INTERNAL_SYSTEM_PROCESSING("결제가 완료되지 않았습니다. 다시 시도해 주세요"),
    FAILED_INTERNAL_SYSTEM_PROCESSING("토스 내부 시스템 처리 작업이 실패했습니다."),
    UNKNOWN_PAYMENT_ERROR("같은 문제가 반복된다면 은행이나 카드사로 문의해주세요.");

    private static final String DEFAULT_MESSAGE = "결제에 실패했습니다.";

    private final String message;

    PaymentServerErrorCode() {
        this(DEFAULT_MESSAGE);
    }

    PaymentServerErrorCode(String message) {
        this.message = message;
    }

    private boolean hasName(String code) {
        return name().equals(code);
    }

    public String getMessage() {
        return message;
    }

    public static Optional<PaymentServerErrorCode> from(String code) {
        return Arrays.stream(values())
                .filter(paymentServerErrorCode -> paymentServerErrorCode.hasName(code))
                .findFirst();
    }
}
