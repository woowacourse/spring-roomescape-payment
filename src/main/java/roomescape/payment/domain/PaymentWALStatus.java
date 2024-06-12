package roomescape.payment.domain;

public enum PaymentWALStatus {
    READY,
    PAY_REQUEST,
    PAY_CONFIRMED,
    PAY_REJECTED,
    CANCEL_CONFIRMED,
    CANCEL_REJECTED
    ;
}
