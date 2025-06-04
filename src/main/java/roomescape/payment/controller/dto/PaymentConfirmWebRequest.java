package roomescape.payment.controller.dto;

import roomescape.payment.service.dto.PaymentConfirmRequest;

public record PaymentConfirmWebRequest(
        String paymentKey,
        String orderId,
        int amount
) {

    public PaymentConfirmRequest toPaymentConfirmRequest() {
        return new PaymentConfirmRequest(paymentKey, orderId, amount);
    }
}
