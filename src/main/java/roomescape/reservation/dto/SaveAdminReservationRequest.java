package roomescape.reservation.dto;

import java.time.LocalDate;
import roomescape.member.model.Member;
import roomescape.reservation.model.Reservation;
import roomescape.reservation.model.ReservationStatus;
import roomescape.reservation.model.ReservationTime;
import roomescape.reservation.model.Theme;

public record SaveAdminReservationRequest(
        LocalDate date,
        Long memberId,
        Long timeId,
        Long themeId,
        String orderId,
        Long amount,
        String paymentKey
) {

    public Reservation toReservation(
            final ReservationTime reservationTime,
            final Theme theme,
            final Member member
    ) {
        return new Reservation(
                ReservationStatus.RESERVATION,
                date,
                reservationTime,
                theme,
                member
        );
    }
}
