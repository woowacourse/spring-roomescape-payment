package roomescape.client.payment.dto;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.math.BigDecimal;
import roomescape.reservation.dto.ReservationRequest;

/**
 * @see <a href="https://docs.tosspayments.com/reference#%EA%B2%B0%EC%A0%9C-%EC%8A%B9%EC%9D%B8">토스 결제 승인 API doc</a>
 */
@Tag(name = "토스 결제 승인 요청", description = "토스 api에게 해당 필드 정보로 결제를 승인해달라고 요청한다.")
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
