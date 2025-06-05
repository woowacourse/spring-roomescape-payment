package roomescape.domain.payment;

public enum TransactionStatusCode {
    SUCCEEDED_PAYMENT,
    FAILED_PAYMENT,
    INVALID_AUTH_CREDENTIALS,
    FAILED_INTERNAL_PROCESSING
}
