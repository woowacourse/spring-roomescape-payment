package roomescape.dto;

import java.time.LocalDate;
import roomescape.domain.Reservation;
import roomescape.domain.ReservationStatus;

public record ReservationResponse(long id,
                                  LocalDate date,
                                  ReservationTimeResponse time,
                                  ThemeResponse theme,
                                  LoginMemberResponse member,
                                  String reservationStatus) {
    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getDate(),
                ReservationTimeResponse.from(reservation.getReservationTime()),
                ThemeResponse.from(reservation.getTheme()),
                LoginMemberResponse.from(reservation.getMember()),
                getReservationStatusMessage(reservation.getReservationStatus())
        );
    }

    private static String getReservationStatusMessage(ReservationStatus reservationStatus) {
        return switch (reservationStatus) {
            case BOOKED -> "예약";
            case WAITING -> "예약 대기";
        };
    }
}
