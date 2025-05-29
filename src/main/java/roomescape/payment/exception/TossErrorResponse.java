package roomescape.payment.exception;

import java.util.Set;

public record TossErrorResponse(String code, String message, String data) {

    private static final Set<String> PAYMENT_ERROR_CODES = Set.of(
            "INVALID_API_KEY",
            "UNAUTHORIZED_KEY",
            "INCORRECT_BASIC_AUTH_FORMAT",
            "INVALID_AUTHORIZE_AUTH"
    );

    public boolean isPaymentError() {
        return code != null && PAYMENT_ERROR_CODES.contains(code);
    }
}
