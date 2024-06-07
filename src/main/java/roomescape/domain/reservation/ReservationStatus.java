package roomescape.domain.reservation;

public enum ReservationStatus {
    RESERVED("예약"),
    WAITING("예약대기"),
    CANCELED("예약취소"),
    PENDING_PAYMENT("결제대기");

    private final String description;

    ReservationStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isReserved() {
        return this == RESERVED;
    }

    public boolean isPendingPayment() {
        return this == PENDING_PAYMENT;
    }
}
