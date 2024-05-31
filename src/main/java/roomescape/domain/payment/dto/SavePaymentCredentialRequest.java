package roomescape.domain.payment.dto;

import roomescape.domain.payment.model.PaymentCredential;

public record SavePaymentCredentialRequest(String orderId, Long amount) {

    public PaymentCredential toPaymentCredential() {
        return new PaymentCredential(orderId, amount);
    }
}
