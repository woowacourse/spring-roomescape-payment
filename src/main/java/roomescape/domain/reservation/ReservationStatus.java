package roomescape.domain.reservation;

import java.util.List;

public enum ReservationStatus {
    RESERVED("예약"),
    WAITING("예약대기"),
    CANCELED("예약취소"),
    PENDING_PAYMENT("결제대기");

    private final String description;

    ReservationStatus(String description) {
        this.description = description;
    }

    public static List<ReservationStatus> getConfirmedStatuses() {
        return List.of(ReservationStatus.RESERVED, ReservationStatus.PENDING_PAYMENT);
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
