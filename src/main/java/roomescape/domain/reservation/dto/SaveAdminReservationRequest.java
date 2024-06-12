package roomescape.domain.reservation.dto;

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
}
