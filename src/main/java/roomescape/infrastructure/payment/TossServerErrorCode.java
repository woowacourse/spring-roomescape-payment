package roomescape.infrastructure.payment;

import java.util.Arrays;

public enum TossServerErrorCode {
    INVALID_REQUEST,
    INVALID_API_KEY,
    NOT_FOUND_TERMINAL_ID,
    INVALID_AUTHORIZE_AUTH,
    INVALID_UNREGISTERED_SUBMALL,
    NOT_REGISTERED_BUSINESS,
    UNAPPROVED_ORDER_ID,
    UNAUTHORIZED_KEY,
    REJECT_CARD_COMPANY,
    FORBIDDEN_REQUEST,
    INCORRECT_BASIC_AUTH_FORMAT,
    NOT_FOUND_PAYMENT,
    NOT_FOUND_PAYMENT_SESSION,
    FAILED_PAYMENT_INTERNAL_SYSTEM_PROCESSING,
    FAILED_INTERNAL_SYSTEM_PROCESSING,
    UNKNOWN_PAYMENT_ERROR,
    ;

    public static boolean isInternalError(String code) {
        return Arrays.stream(TossServerErrorCode.values())
                .anyMatch(tossStatusCode -> tossStatusCode.name().equals(code));
    }
}
