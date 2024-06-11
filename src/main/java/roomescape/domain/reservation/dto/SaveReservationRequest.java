package roomescape.domain.reservation.dto;

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
}
