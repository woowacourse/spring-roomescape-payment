package roomescape.reservation.domain;

public enum ReservationStatus {

    RESERVED("예약 확정"),
    PAYMENT_PENDING("결제 대기");

    private final String name;

    ReservationStatus(String name) {
        this.name = name;
    }
}
