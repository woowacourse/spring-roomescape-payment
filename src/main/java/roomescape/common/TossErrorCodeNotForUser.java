package roomescape.common;

import java.util.Arrays;

public enum TossErrorCodeNotForUser {

    INVALID_API_KEY,
    UNAUTHORIZED_KEY,
    INCORRECT_BASIC_AUTH_FORMAT;

    public static boolean hasContain(String code) {
        return Arrays.stream(values())
                .anyMatch(tossErrorCodeNotForUser -> tossErrorCodeNotForUser.name().equals(code));
    }
}
