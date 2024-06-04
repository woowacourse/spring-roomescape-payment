package roomescape.reservation.dto.request;

import jakarta.validation.constraints.NotNull;
import roomescape.payment.domain.NewPayment;

public record PaymentConfirmRequest(
        @NotNull(message = "결제 키는 비어있을 수 없습니다.")
        String paymentKey,
        @NotNull(message = "주문 Id는 비어있을 수 없습니다.")
        String orderId,
        @NotNull(message = "결제 금액은 비어있을 수 없습니다.")
        Long amount,
        @NotNull(message = "결제 타입은 비어있을 수 없습니다.")
        String paymentType) {

    public NewPayment toNewPayment() {
        return new NewPayment(paymentKey, orderId, amount, paymentType);
    }
}
