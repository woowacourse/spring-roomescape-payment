package roomescape.payment.exception;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum TossPaymentErrorCodeForServer {
    INVALID_API_KEY,
    INVALID_REQUEST,
    NOT_FOUND_TERMINAL_ID,
    INVALID_AUTHORIZE_AUTH,
    INVALID_UNREGISTERED_SUBMALL,
    NOT_REGISTERED_BUSINESS,
    UNAPPROVED_ORDER_ID,
    UNAUTHORIZED_KEY,
    INCORRECT_BASIC_AUTH_FORMAT,
    FORBIDDEN_REQUEST,
    NOT_FOUND_PAYMENT,
    NOT_FOUND_PAYMENT_SESSION,
    FAILED_INTERNAL_SYSTEM_PROCESSING,
    REJECT_CARD_COMPANY;

    private static final Set<String> ERROR_CODE_NAMES =
            Arrays.stream(values())
                    .map(Enum::name)
                    .collect(Collectors.toSet());

    public static boolean contains(String errorCodeName) {
        return ERROR_CODE_NAMES.contains(errorCodeName);
    }
}

