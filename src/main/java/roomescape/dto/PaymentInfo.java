package roomescape.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.domain.*;

@Schema(description = "Payment Information Model")
public record PaymentInfo(@Schema(description = "Amount of the payment", example = "10000")
                          Long amount,
                          @Schema(description = "Order ID for the payment", example = "ORDER123")
                          String orderId,
                          @Schema(description = "Payment key", example = "PAYMENT_KEY_123")
                          String paymentKey) {

    public PaymentInfo{
        isValid(amount, orderId, paymentKey);
    }

    public Payment toEntity() {
        return new Payment(paymentKey, orderId, amount);
    }

    private void isValid(Long amount, String orderId, String paymentKey) {
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("옳지 않은 amount 값입니다.");
        }

        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("옳지 않은 orderId입니다.");
        }

        if (paymentKey == null || paymentKey.isBlank()) {
            throw new IllegalArgumentException("옳지 않은 paymentKey입니다.");
        }
    }
}
