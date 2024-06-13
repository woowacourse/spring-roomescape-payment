package roomescape.system.exception.model;

import java.util.Arrays;

public enum InvalidTossErrorCode {
    INVALID_API_KEY("INVALID_API_KEY"),
    INVALID_AUTHORIZE_AUTH("INVALID_AUTHORIZE_AUTH"),
    UNAUTHORIZED_KEY("UNAUTHORIZED_KEY"),
    INCORRECT_BASIC_AUTH_FORMAT("INCORRECT_BASIC_AUTH_FORMAT")
    ;

    private final String name;

    InvalidTossErrorCode(String name) {
        this.name = name;
    }

    public static boolean canConvert(String errorCode) {
        return Arrays.stream(values())
                .anyMatch(tossErrorFilter -> tossErrorFilter.name.equals(errorCode));
    }
}
