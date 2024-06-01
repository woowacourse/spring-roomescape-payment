package roomescape.client.payment.dto;

import roomescape.registration.domain.reservation.dto.ReservationRequest;

public record PaymentConfirmToTossDto(
        String orderId,
        Integer amount,
        String paymentKey) {

    public static PaymentConfirmToTossDto from(ReservationRequest reservationRequest) {
        return new PaymentConfirmToTossDto(
                reservationRequest.orderId(),
                reservationRequest.amount(),
                reservationRequest.paymentKey()
        );
    }
}
