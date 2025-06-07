package roomescape.presentation.api.payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import roomescape.application.payment.toss.dto.TossPaymentValidationCommand;

public record TossPaymentValidationRequest(
        @NotBlank(message = "orderId는 필수입니다.")
        String orderId,
        @NotNull(message = "amount는 필수입니다.")
        Long amount
) {

    public TossPaymentValidationCommand toCommand() {
        return new TossPaymentValidationCommand(orderId, amount);
    }
}
