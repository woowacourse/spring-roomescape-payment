package roomescape.client.payment.dto;

import java.math.BigDecimal;
import roomescape.registration.domain.reservation.dto.ReservationRequest;

/**
 * @see <a href="https://docs.tosspayments.com/reference#%EA%B2%B0%EC%A0%9C-%EC%8A%B9%EC%9D%B8">토스 결제 승인 API doc</a>
 */
public record PaymentConfirmToTossDto(
        String orderId,
        BigDecimal amount,
        String paymentKey) {

    public static PaymentConfirmToTossDto from(ReservationRequest reservationRequest) {
        return new PaymentConfirmToTossDto(
                reservationRequest.orderId(),
                reservationRequest.amount(),
                reservationRequest.paymentKey()
        );
    }
}
