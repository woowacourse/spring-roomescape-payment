package roomescape.payment.toss.interceptor;

import java.util.Arrays;

public enum TossPaymentTemporaryErrorCode {
    PROVIDER_ERROR("PROVIDER_ERROR"),
    FAILED_PAYMENT_INTERNAL_SYSTEM_PROCESSING("FAILED_PAYMENT_INTERNAL_SYSTEM_PROCESSING"),
    FAILED_INTERNAL_SYSTEM_PROCESSING("FAILED_INTERNAL_SYSTEM_PROCESSING"),
    ;

    private final String code;

    TossPaymentTemporaryErrorCode(String code) {
        this.code = code;
    }

    public static boolean isTemporaryError(String errorCode) {
        return Arrays.stream(values())
                .anyMatch(error -> error.code.equals(errorCode));
    }
}
