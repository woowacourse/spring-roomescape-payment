package roomescape.web.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import roomescape.service.request.PaymentSaveDto;

public record PaymentRequest(
        @NotNull Long reservationId,
        @NotBlank String paymentKey,
        @NotBlank String orderId,
        @NotNull Long amount) {

    public PaymentSaveDto toPaymentSaveDto(Long memberId) {
        return new PaymentSaveDto(memberId, reservationId, paymentKey, orderId, amount);
    }
}
