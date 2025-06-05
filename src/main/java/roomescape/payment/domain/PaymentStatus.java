package roomescape.payment.domain;

public enum PaymentStatus {

    PENDING, APPROVED, FAILED;

    public boolean isFinished() {
        return this != PENDING;
    }
}
