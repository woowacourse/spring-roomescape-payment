package roomescape.payment.dto;

import roomescape.reservation.domain.dto.ReservationWithPaymentDto;

public record PaymentRequestDto(String paymentKey, String orderId, int amount) {

    public static PaymentRequestDto ofReservationWithPaymentDto(ReservationWithPaymentDto requestDto) {
        return new PaymentRequestDto(
                requestDto.paymentKey(),
                requestDto.orderId(),
                requestDto.amount()
        );
    }
}
