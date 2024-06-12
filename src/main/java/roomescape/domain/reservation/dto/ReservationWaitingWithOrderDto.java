package roomescape.domain.reservation.dto;

import roomescape.domain.member.dto.MemberDto;
import roomescape.domain.reservation.model.ReservationDate;
import roomescape.domain.reservation.model.ReservationWaitingWithOrder;

public record ReservationWaitingWithOrderDto(
        Long id,
        int order,
        boolean paymentAvailable,
        ReservationDate date,
        ReservationTimeDto time,
        ThemeDto theme,
        MemberDto member
) {
    public static ReservationWaitingWithOrderDto of(final ReservationWaitingWithOrder reservationWaitingWithOrder, final boolean paymentAvailable) {
        return new ReservationWaitingWithOrderDto(
                reservationWaitingWithOrder.reservationWaiting().getId(),
                reservationWaitingWithOrder.order(),
                paymentAvailable,
                reservationWaitingWithOrder.reservationWaiting().getDate(),
                ReservationTimeDto.from(reservationWaitingWithOrder.reservationWaiting().getTime()),
                ThemeDto.from(reservationWaitingWithOrder.reservationWaiting().getTheme()),
                MemberDto.from(reservationWaitingWithOrder.reservationWaiting().getMember())
        );
    }
}
