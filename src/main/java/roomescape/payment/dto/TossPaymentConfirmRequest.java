package roomescape.payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record TossPaymentConfirmRequest(
        @NotBlank String paymentKey,
        @NotBlank String orderId,
        @NotNull @Positive BigDecimal amount
) implements PaymentConfirmRequest {
    
    @Override
    public String getOrderId() {
        return orderId;
    }

    @Override
    public BigDecimal getAmount() {
        return amount;
    }
}
