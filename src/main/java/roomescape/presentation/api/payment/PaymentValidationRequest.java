package roomescape.presentation.api.payment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import roomescape.application.payment.dto.PaymentValidationCommand;

public record PaymentValidationRequest(
        @NotBlank(message = "orderId는 필수입니다.")
        String orderId,
        @NotNull(message = "amount는 필수입니다.")
        Long amount
) {

    public PaymentValidationCommand toCommand() {
        return new PaymentValidationCommand(orderId, amount);
    }
}
