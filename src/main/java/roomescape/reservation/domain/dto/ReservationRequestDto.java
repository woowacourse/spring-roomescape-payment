package roomescape.reservation.domain.dto;

import roomescape.payment.global.domain.PgPayment;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.theme.domain.Theme;
import roomescape.user.domain.User;

import java.time.LocalDate;

public record ReservationRequestDto(LocalDate date,
                                    Long timeId,
                                    Long themeId) {

    public Reservation toEntity(ReservationTime reservationTime, Theme theme, User user, PgPayment pgPayment) {
        return Reservation.of(date, ReservationStatus.BOOKED, reservationTime, theme, user, pgPayment);
    }

    public static ReservationRequestDto ofReservationWithPaymentDto(ReservationWithPaymentDto reservationWithPaymentDto) {
        return new ReservationRequestDto(
                reservationWithPaymentDto.date(),
                reservationWithPaymentDto.timeId(),
                reservationWithPaymentDto.themeId()
        );
    }
}


