package roomescape.payment.infrastructure;

import java.util.Arrays;

public enum TossPaymentErrorMessage {
    UNAUTHORIZED_KEY,
    INCORRECT_BASIC_AUTH_FORMAT,
    INVALID_API_KEY,
    NOT_FOUND_TERMINAL_ID,
    INVALID_UNREGISTERED_SUBMALL,
    NOT_REGISTERED_BUSINESS,
    FORBIDDEN_REQUEST,
    FAILED_INTERNAL_SYSTEM_PROCESSING;

    public static boolean contains(final String code) {
        return Arrays.stream(values())
                .anyMatch(value -> value.toString().equals(code));
    }
}
