package roomescape.client.payment.dto;

import roomescape.registration.domain.reservation.dto.ReservationRequest;

import java.io.Serializable;

public record PaymentConfirmationToTossDto(
        String orderId,
        Integer amount,
        String paymentKey) implements Serializable {

    public static PaymentConfirmationToTossDto from(ReservationRequest reservationRequest) {
        return new PaymentConfirmationToTossDto(
                reservationRequest.orderId(),
                reservationRequest.amount(),
                reservationRequest.paymentKey()
        );
    }
}
