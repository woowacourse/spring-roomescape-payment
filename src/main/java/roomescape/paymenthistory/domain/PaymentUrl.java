package roomescape.paymenthistory.domain;

import roomescape.paymenthistory.exception.PaymentException.PaymentServerError;

public class PaymentUrl {

    private final String paymentUrl;

    public PaymentUrl(String paymentUrl) {
        this.paymentUrl = paymentUrl;
        validation();
    }

    public String getPaymentUrl() {
        return paymentUrl;
    }

    private void validation() {
        if (paymentUrl == null) {
            throw new PaymentServerError();
        }
    }
}
