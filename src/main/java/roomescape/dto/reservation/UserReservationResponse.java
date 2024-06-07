package roomescape.dto.reservation;

import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.waiting.Waiting;

public record UserReservationResponse(
        Long id,
        String theme,
        LocalDate date,
        LocalTime time,
        String status,
        Long paymentId
) {

    private static final String RESERVED = "예약";
    private static final String WAITING_ORDER = "%d번째 예약 대기";
    private static final String PENDING = "결제 대기";

    public static UserReservationResponse create(Reservation reservation) {
        return new UserReservationResponse(
                reservation.getId(),
                reservation.getTheme().getThemeName(),
                reservation.getDate(),
                reservation.getTime().getStartAt(),
                RESERVED,
                reservation.getPayment().getId()
        );
    }

    public static UserReservationResponse createByWaiting(Waiting waiting) {
        Reservation reservation = waiting.getReservation();
        return new UserReservationResponse(
                reservation.getId(),
                reservation.getTheme().getThemeName(),
                reservation.getDate(),
                reservation.getTime().getStartAt(),
                String.format(WAITING_ORDER, waiting.getWaitingOrderValue()),
                null
        );
    }

    public static UserReservationResponse createPending(final Reservation reservation) {
        return new UserReservationResponse(
                reservation.getId(),
                reservation.getTheme().getThemeName(),
                reservation.getDate(),
                reservation.getTime().getStartAt(),
                PENDING,
                null
        );
    }

    public boolean isReserved() {
        return this.status.equals(RESERVED);
    }

    public boolean isPending() {
        return this.status.equals(PENDING);
    }

    public boolean isWaiting() {
        return !this.status.equals(RESERVED) && !this.status.equals(PENDING);
    }
}
