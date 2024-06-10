package roomescape.client.payment.dto;

import java.math.BigDecimal;
import roomescape.registration.domain.reservation.dto.ReservationRequest;

/**
 * @see <a href="https://docs.tosspayments.com/reference#%EA%B2%B0%EC%A0%9C-%EC%8A%B9%EC%9D%B8">토스 결제 승인 API doc</a>
 */
public record TossPaymentConfirmRequest(
        String orderId,
        BigDecimal amount,
        String paymentKey) {

    public static TossPaymentConfirmRequest from(ReservationRequest reservationRequest) {
        return new TossPaymentConfirmRequest(
                reservationRequest.orderId(),
                reservationRequest.amount(),
                reservationRequest.paymentKey()
        );
    }
}
