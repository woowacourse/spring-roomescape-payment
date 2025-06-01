package roomescape.payment.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record PrePaymentRequest(
        @NotBlank String orderId,
        @NotNull BigDecimal amount
) {
}
