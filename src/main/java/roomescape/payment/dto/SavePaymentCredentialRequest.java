package roomescape.payment.dto;

import roomescape.payment.model.PaymentCredential;

public record SavePaymentCredentialRequest(String orderId, Long amount) {

    public PaymentCredential toPaymentCredential() {
        return new PaymentCredential(orderId, amount);
    }
}
