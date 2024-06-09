package roomescape.domain.reservation.dto;

import roomescape.domain.member.model.Member;
import roomescape.domain.reservation.model.Reservation;
import roomescape.domain.reservation.model.ReservationStatus;
import roomescape.domain.reservation.model.ReservationTime;
import roomescape.domain.reservation.model.Theme;

import java.time.LocalDate;

public record SaveAdminReservationRequest(
        LocalDate date,
        Long memberId,
        Long timeId,
        Long themeId,
        String orderId,
        String orderName,
        Long amount,
        String paymentKey
) {
    public SaveAdminReservationRequest setMemberId(final Long memberId) {
        return new SaveAdminReservationRequest(
                date,
                memberId,
                timeId,
                themeId,
                orderId,
                orderName,
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
