package roomescape.reservation.dto.response;

import roomescape.reservation.domain.Reservation;

import java.time.LocalDate;

public record WaitingReservationResponse(
        Long id,
        String memberName,
        LocalDate date,
        ReservationTimeResponse time,
        ReservedThemeResponse theme,
        String status
) {

    public static WaitingReservationResponse from(Reservation reservation) {
        return new WaitingReservationResponse(
                reservation.getId(),
                reservation.getMemberName(),
                reservation.getDate(),
                ReservationTimeResponse.from(reservation.getTime()),
                ReservedThemeResponse.from(reservation.getTheme()),
                status(reservation)
        );
    }

    private static String status(Reservation reservation) {
        if (reservation.isWaiting()) {
            return "예약 대기";
        }
        return "결제 대기";
    }
}
