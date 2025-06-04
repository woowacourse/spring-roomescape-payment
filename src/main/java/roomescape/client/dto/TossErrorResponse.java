package roomescape.client.dto;

import java.util.List;

public record TossErrorResponse(String message, String code) {

    private static final List<String> MASKED_ERROR_CODES = List.of("INVALID_REQUEST", "INVALID_API_KEY", "UNAUTHORIZED_KEY",
            "INCORRECT_BASIC_AUTH_FORMAT", "NOT_FOUND_PAYMENT", "NOT_FOUND_PAYMENT_SESSION");
    private static final String GENERIC_ERROR_MESSAGE = "결제에 실패했습니다. 잠시 후 다시 시도해주세요.";

    public TossErrorResponse(final String message, final String code) {
        this.code = code;
        if (MASKED_ERROR_CODES.contains(code)) {
            this.message = GENERIC_ERROR_MESSAGE;
            return;
        }
        this.message = message;
    }
}
