package roomescape.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

public record PaymentResponse(
        @Schema(description = "결제 키", example = "tgen_20240528211")
        String paymentKey,
        @Schema(description = "결제 금액", example = "128000")
        BigDecimal amount
) {
}
