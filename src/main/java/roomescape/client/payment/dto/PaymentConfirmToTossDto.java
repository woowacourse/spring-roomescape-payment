package roomescape.client.payment.dto;

import java.io.Serializable;
import roomescape.registration.domain.reservation.dto.ReservationRequest;

public record PaymentConfirmToTossDto(String orderId, Integer amount, String paymentKey) implements Serializable {

    public static PaymentConfirmToTossDto from(ReservationRequest reservationRequest) {
        return new PaymentConfirmToTossDto(
                reservationRequest.orderId(),
                reservationRequest.amount(),
                reservationRequest.paymentKey()
        );
    }
}
