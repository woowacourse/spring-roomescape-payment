package roomescape.payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public interface PaymentConfirmRequest {
    @NotBlank
    String getOrderId();

    @NotNull
    @Positive
    BigDecimal getAmount();
}
