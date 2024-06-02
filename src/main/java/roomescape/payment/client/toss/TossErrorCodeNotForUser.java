package roomescape.payment.client.toss;


import java.util.Arrays;

public enum TossErrorCodeNotForUser {
    INVALID_API_KEY,
    NOT_FOUND_TERMINAL_ID,
    INVALID_AUTHORIZE_AUTH,
    INVALID_UNREGISTERED_SUBMALL,
    NOT_REGISTERED_BUSINESS,
    UNAPPROVED_ORDER_ID,
    UNAUTHORIZED_KEY,
    FORBIDDEN_REQUEST,
    INCORRECT_BASIC_AUTH_FORMAT,
    ;

    public static boolean hasContains(final String message) {
        return Arrays.stream(TossErrorCodeNotForUser.values())
                .anyMatch(tossErrorCodeNotForUser -> tossErrorCodeNotForUser.name().equals(message));
    }
}
