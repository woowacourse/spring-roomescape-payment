package roomescape.payment.exception;

public record TossErrorResponse(String code, String message, String data) {

    public boolean isPaymentError() {
        return code.equals("INVALID_API_KEY")
                || code.equals("UNAUTHORIZED_KEY")
                || code.equals("INCORRECT_BASIC_AUTH_FORMAT")
                || code.equals("INVALID_AUTHORIZE_AUTH");
    }
}
