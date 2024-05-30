package roomescape.payment.exception;

public enum PaymentServerExceptionCode {
    UNAUTHORIZED_KEY,
    INCORRECT_BASIC_AUTH_FORMAT,
    INVALID_ORDER_ID,
    FAILED_INTERNAL_SYSTEM_PROCESSING;

    public static boolean isServerError(String errorCode) {
        return valueOf(errorCode) != null;
    }
}
