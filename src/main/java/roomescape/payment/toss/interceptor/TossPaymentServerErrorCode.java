package roomescape.payment.toss.interceptor;

import java.util.Arrays;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum TossPaymentServerErrorCode {

    INVALID_API_KEY("INVALID_API_KEY"),
    NOT_FOUND_TERMINAL_ID("NOT_FOUND_TERMINAL_ID"),
    INVALID_AUTHORIZE_AUTH("INVALID_AUTHORIZE_AUTH"),
    INVALID_UNREGISTERED_SUBMALL("INVALID_UNREGISTERED_SUBMALL"),
    UNAUTHORIZED_KEY("UNAUTHORIZED_KEY"),
    FAILED_INTERNAL_SYSTEM_PROCESSING("FAILED_INTERNAL_SYSTEM_PROCESSING"),
    UNKNOWN_PAYMENT_ERROR("UNKNOWN_PAYMENT_ERROR"),
    INCORRECT_BASIC_AUTH_FORMAT("INCORRECT_BASIC_AUTH_FORMAT"),
    ;

    private final String code;

    public static boolean isServerError(String errorCode) {
        return Arrays.stream(values())
                .anyMatch(error -> error.code.equals(errorCode));
    }
}
