package roomescape.payment.dto;

import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Range;

public record PaymentRequest(
        @NotNull
        String orderId,

        @Range(min = 0, max = Integer.MAX_VALUE)
        int amount,

        @NotNull
        String paymentKey
) {

}
