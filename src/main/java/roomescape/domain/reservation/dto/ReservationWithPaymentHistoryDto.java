package roomescape.domain.reservation.dto;

import roomescape.domain.member.dto.MemberDto;
import roomescape.domain.reservation.model.Reservation;
import roomescape.domain.reservation.model.ReservationDate;
import roomescape.domain.reservation.model.ReservationStatus;

public record ReservationWithPaymentHistoryDto(
        Long id,
        ReservationStatus status,
        ReservationDate date,
        ReservationTimeDto time,
        ThemeDto theme,
        MemberDto member,
        String paymentKey,
        Long totalAmount
) {
    public static ReservationWithPaymentHistoryDto of(
            final Reservation reservation,
            final String paymentKey,
            final Long totalAmount

    ) {
        return new ReservationWithPaymentHistoryDto(
                reservation.getId(),
                reservation.getStatus(),
                reservation.getDate(),
                ReservationTimeDto.from(reservation.getTime()),
                ThemeDto.from(reservation.getTheme()),
                MemberDto.from(reservation.getMember()),
                paymentKey,
                totalAmount
        );
    }
}
