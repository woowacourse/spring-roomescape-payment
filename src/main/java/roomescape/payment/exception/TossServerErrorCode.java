package roomescape.payment.exception;

import java.util.Arrays;

public enum TossServerErrorCode {
    INVALID_KEY("INVALID_API_KEY"),
    INVALID_REQUEST("INVALID_REQUEST"),
    INVALID_AUTHORIZE_AUTH("INVALID_AUTHORIZE_AUTH"),
    INCORRECT_AUTH_FORMAT("INCORRECT_BASIC_AUTH_FORMAT");

    private final String code;

    TossServerErrorCode(final String code) {
        this.code = code;
    }

    public static boolean isServerError(String code) {
        return Arrays.stream(TossServerErrorCode.values())
                .anyMatch(tossServerErrorCode -> tossServerErrorCode.code.equals(code));
    }
}
