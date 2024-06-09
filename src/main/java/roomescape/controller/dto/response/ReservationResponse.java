package roomescape.controller.dto.response;

import java.time.LocalDate;

import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationStatus;

public record ReservationResponse(
        Long id,
        MemberResponse member,
        LocalDate date,
        TimeResponse time,
        ThemeResponse theme,
        PaymentResponse payment,
        ReservationStatus status) {
    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                MemberResponse.from(reservation.getMember()),
                reservation.getDate(),
                TimeResponse.from(reservation.getTime()),
                ThemeResponse.from(reservation.getTheme()),
                PaymentResponse.from(reservation.getPayment()),
                reservation.getStatus()
        );
    }
}
