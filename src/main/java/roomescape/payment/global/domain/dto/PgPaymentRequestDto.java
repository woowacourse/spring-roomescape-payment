package roomescape.payment.global.domain.dto;

import roomescape.reservation.domain.dto.ReservationWithPaymentDto;

public record PgPaymentRequestDto(String paymentKey, String orderId, int amount, String paymentType) {

    public static PgPaymentRequestDto ofReservationWithPaymentDto(ReservationWithPaymentDto requestDto) {
        return new PgPaymentRequestDto(
                requestDto.paymentKey(),
                requestDto.orderId(),
                requestDto.amount(),
                requestDto.paymentType()
        );
    }
}
