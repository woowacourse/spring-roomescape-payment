package roomescape.payment.domain;

import java.util.Arrays;

public enum ConfidentialApproveExceptionCode {

    INVALID_API_KEY,
    INVALID_AUTHORIZE_AUTH,
    UNAUTHORIZED_KEY;

    public static boolean isConfidential(String exceptionCode) {
        return Arrays.stream(values())
                .anyMatch(value -> value.name().equals(exceptionCode));
    }
}
