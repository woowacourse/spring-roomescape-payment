package roomescape.payment.exception;

import java.util.Arrays;

public enum TossErrorCode {

    INVALID_REQUEST,
    INVALID_API_KEY,
    INVALID_AUTHORIZE_AUTH,
    UNAUTHORIZED_KEY,
    INCORRECT_BASIC_AUTH_FORMAT;

    public static boolean isServerError(final String code) {
        return Arrays.stream(values()).anyMatch(e -> e.name().equals(code));
    }
}
