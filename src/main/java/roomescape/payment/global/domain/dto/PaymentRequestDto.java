package roomescape.payment.global.domain.dto;

import roomescape.reservation.domain.dto.ReservationWithPaymentDto;

public record PaymentRequestDto(String paymentKey, String orderId, int amount, String paymentType) {

    public static PaymentRequestDto ofReservationWithPaymentDto(ReservationWithPaymentDto requestDto) {
        return new PaymentRequestDto(
                requestDto.paymentKey(),
                requestDto.orderId(),
                requestDto.amount(),
                requestDto.paymentType()
        );
    }
}
