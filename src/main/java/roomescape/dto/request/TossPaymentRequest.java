package roomescape.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.domain.Payment;

import java.math.BigDecimal;


public record TossPaymentRequest(
        @Schema(description = "결제 키") String paymentKey,
        @Schema(description = "주문 번호", minimum = "6", maximum = "64") String orderId,
        @Schema(description = "금액") BigDecimal amount
) {
    public static TossPaymentRequest from(Payment payment) {
        return new TossPaymentRequest(payment.getPaymentKey(), payment.getOrderId(), payment.getAmount());
    }
}
