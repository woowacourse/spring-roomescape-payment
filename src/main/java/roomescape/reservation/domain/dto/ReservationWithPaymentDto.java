package roomescape.reservation.domain.dto;

import roomescape.payment.dto.PaymentRequestDto;

import java.time.LocalDate;

public record ReservationWithPaymentDto(
        LocalDate date,
        Long timeId,
        Long themeId,
        String paymentKey,
        String orderId,
        int amount
) {

    public ReservationRequestDto toReservationRequestDto() {
        return new ReservationRequestDto(date, timeId, themeId);
    }

    public PaymentRequestDto toPaymentRequestDto() {
        return new PaymentRequestDto(paymentKey, orderId, amount);
    }
}


