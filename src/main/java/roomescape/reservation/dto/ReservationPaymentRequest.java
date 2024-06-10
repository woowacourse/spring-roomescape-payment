package roomescape.reservation.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import roomescape.payment.dto.PaymentConfirmRequest;

public record ReservationPaymentRequest(
        @NotNull(message = "예약 id가 존재하지 않습니다.") Long reservationId,
        @NotNull(message = "paymentkey가 존재하지 않습니다.") String paymentKey,
        @NotNull(message = "orderId가 존재하지 않습니다.") String orderId,
        @NotNull(message = "Amount가 존재하지 않습니다.") @Positive Long amount
) {

    public PaymentConfirmRequest extractPaymentConfirmRequest() {
        return new PaymentConfirmRequest(paymentKey, orderId, amount);
    }
}
