package roomescape.payment.infrastructure.toss.exception;

import java.util.Set;

public record TossErrorResponse(String code, String message) {
    public boolean hasNonUserFacingMessage() {
        //https://docs.tosspayments.com/reference/error-codes#%EA%B2%B0%EC%A0%9C-%EC%8A%B9%EC%9D%B8
        return Set.of(
                "INVALID_API_KEY",
                "UNAUTHORIZED_KEY",
                "INCORRECT_BASIC_AUTH_FORMAT",
                "INVALID_AUTHORIZE_AUTH"
        ).contains(code);
    }
}
