package roomescape.payment.application.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record PrePaymentRequest(
        @NotNull String orderId,
        @NotNull BigDecimal amount
) {
}
