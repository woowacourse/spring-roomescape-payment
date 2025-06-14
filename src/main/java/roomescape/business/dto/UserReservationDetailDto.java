package roomescape.business.dto;

import roomescape.business.model.entity.Reservation;

public record UserReservationDetailDto(
        Reservation reservation,
        Long aheadCount,
        String paymentKey,
        Long totalAmount
) {
}
