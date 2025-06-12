package roomescape.payment.application.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record PrePaymentValidRequest(
        @NotNull String orderId,
        @NotNull String orderName,
        @NotNull BigDecimal amount
) {
}
