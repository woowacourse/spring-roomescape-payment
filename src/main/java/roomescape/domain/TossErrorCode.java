package roomescape.domain;

import java.util.Arrays;

public enum TossErrorCode {
    INVALID_API_KEY,
    UNAUTHORIZED_KEY,
    INCORRECT_BASIC_AUTH_FORMAT;

    public static boolean containsCode(String code) {
        return Arrays.stream(TossErrorCode.values())
                .anyMatch(tossErrorCode -> tossErrorCode.name().equals(code));
    }
}
