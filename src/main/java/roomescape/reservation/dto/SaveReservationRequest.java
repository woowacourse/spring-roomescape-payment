package roomescape.reservation.dto;

import roomescape.member.model.Member;
import roomescape.reservation.model.Reservation;
import roomescape.reservation.model.ReservationStatus;
import roomescape.reservation.model.ReservationTime;
import roomescape.reservation.model.Theme;

import java.time.LocalDate;

public record SaveReservationRequest(
        LocalDate date,
        Long memberId,
        Long timeId,
        Long themeId,
        String orderId,
        Long amount,
        String paymentKey
) {
    public SaveReservationRequest setMemberId(final Long memberId) {
        return new SaveReservationRequest(
                date,
                memberId,
                timeId,
                themeId,
                orderId,
                amount,
                paymentKey
        );
    }

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
