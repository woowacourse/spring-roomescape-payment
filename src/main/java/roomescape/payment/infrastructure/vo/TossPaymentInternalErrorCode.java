package roomescape.payment.infrastructure.vo;

public enum TossPaymentInternalErrorCode {
    INCORRECT_BASIC_AUTH_FORMAT,
    INVALID_API_KEY,
    INVALID_AUTHORIZE_AUTH,
    INVALID_IDEMPOTENCY_KEY,
    IDEMPOTENT_REQUEST_PROCESSING
    ;

    public String getCode() {
        return this.name();
    }
}
