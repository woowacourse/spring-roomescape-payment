package roomescape.reservation.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.dto.WaitingWithRank;

public record ReservationMineResponse(Long reservationId, String theme, LocalDate date, LocalTime time, String status) {

    private static final String RESERVED = "예약";
    private static final String WAITING = "%d번째 예약대기";

    public static ReservationMineResponse from(final Reservation reservation) {
        return new ReservationMineResponse(
            reservation.getId(),
            reservation.getTheme().getName(),
            reservation.getDate(),
            reservation.getTime().getStartAt(),
            RESERVED
        );
    }

    public static ReservationMineResponse from(final WaitingWithRank waitingWithRank) {
        Reservation reservation = waitingWithRank.getWaiting().getReservation();
        return new ReservationMineResponse(
            reservation.getId(),
            reservation.getTheme().getName(),
            reservation.getDate(),
            reservation.getTime().getStartAt(),
            formatRank(waitingWithRank.getRank())
        );
    }

    private static String formatRank(Long rank) {
        return String.format(WAITING, rank);
    }
}
