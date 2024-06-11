package roomescape.domain.payment;

public enum PaymentStatus {
    COMPLETE,
    REFUNDED
    ;

    public boolean isCompleted() {
        return this == COMPLETE;
    }
}
