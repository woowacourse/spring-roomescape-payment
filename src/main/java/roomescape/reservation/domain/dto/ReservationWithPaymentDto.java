package roomescape.reservation.domain.dto;

import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.theme.domain.Theme;
import roomescape.user.domain.User;

import java.time.LocalDate;

public record ReservationWithPaymentDto(LocalDate date,
                                        Long timeId,
                                        Long themeId,
                                        String paymentKey,
                                        String orderId,
                                        int amount,
                                        String paymentType) {

    public Reservation toEntity(ReservationTime reservationTime, Theme theme, User user) {
        return Reservation.of(date, ReservationStatus.BOOKED, reservationTime, theme, user);
    }
}


