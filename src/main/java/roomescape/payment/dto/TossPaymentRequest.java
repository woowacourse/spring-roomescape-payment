package roomescape.payment.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import org.hibernate.validator.constraints.Range;

public record TossPaymentRequest(
        @NotNull
        String orderId,

        @Range(min = 0, max = Integer.MAX_VALUE)
        BigDecimal amount,

        @NotNull
        String paymentKey
) {

}
