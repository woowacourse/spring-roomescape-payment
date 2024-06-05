package roomescape.web.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import roomescape.service.request.PaymentApproveDto;

public record PaymentRequest(
        @NotNull Long reservationId,
        @NotBlank String paymentKey,
        @NotBlank String orderId,
        @NotNull Long amount) {

    public PaymentApproveDto toPaymentApproveDto() {
        return new PaymentApproveDto(paymentKey, orderId, amount);
    }
}
