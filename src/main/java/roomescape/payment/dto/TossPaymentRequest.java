package roomescape.payment.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record TossPaymentRequest(
        @NotNull
        String orderId,

        BigDecimal amount,

        @NotNull
        String paymentKey
) {

}
