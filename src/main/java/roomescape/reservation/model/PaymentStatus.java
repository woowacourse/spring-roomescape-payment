package roomescape.reservation.model;

public enum PaymentStatus {

    DONE("결제 완료"),
    WAITING("결제 대기"),
    ;

    private final String description;

    PaymentStatus(String description) {
        this.description = description;
    }

    public boolean isPaid() {
        return DONE.equals(this);
    }

    public String getDescription() {
        return description;
    }
}
