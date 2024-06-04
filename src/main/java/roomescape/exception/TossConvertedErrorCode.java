package roomescape.exception;

import java.util.Arrays;

public enum TossConvertedErrorCode {
    INVALID_API_KEY("INVALID_API_KEY"),
    INVALID_AUTHORIZE_AUTH("INVALID_AUTHORIZE_AUTH"),
    UNAUTHORIZED_KEY("UNAUTHORIZED_KEY"),
    INCORRECT_BASIC_AUTH_FORMAT("INCORRECT_BASIC_AUTH_FORMAT")
    ;

    private final String name;

    TossConvertedErrorCode(String name) {
        this.name = name;
    }

    public static boolean canConvert(String errorCode) {
        return Arrays.stream(values())
                .anyMatch(tossErrorFilter -> tossErrorFilter.name.equals(errorCode));
    }
}
