package roomescape.payment.infrastructure.toss.exception;

import java.util.Set;

public record TossErrorResponse(String code, String message) {
    public boolean hasNonUserFacingMessage() {
        return Set.of(
                "INVALID_API_KEY",
                "UNAUTHORIZED_KEY",
                "INCORRECT_BASIC_AUTH_FORMAT",
                "INVALID_AUTHORIZE_AUTH"
        ).contains(code);
    }
}
