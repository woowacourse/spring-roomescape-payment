package roomescape.payment.infraStructure.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.reservation.service.dto.request.ReservationWithPaymentRequest;

@Schema(name = "ConfirmPaymentRequest(결제 요청 DTO)")
public record ConfirmPaymentRequest(
        String paymentKey,
        String orderId,
        Integer amount
) {
    public static ConfirmPaymentRequest from(ReservationWithPaymentRequest paymentRequest) {
        return new ConfirmPaymentRequest(
                paymentRequest.paymentKey(),
                paymentRequest.orderId(),
                paymentRequest.amount()
        );
    }
}
