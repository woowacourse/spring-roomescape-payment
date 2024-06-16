package roomescape.payment.domain;

public enum PaymentStatus {
    PAID(true),
    REFUND(false),
    ;

    private final boolean isPaid;

    PaymentStatus(boolean isPaid) {
        this.isPaid = isPaid;
    }

    public boolean isPaid() {
        return isPaid;
    }
}
