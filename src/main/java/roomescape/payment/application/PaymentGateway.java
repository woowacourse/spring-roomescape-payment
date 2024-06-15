package roomescape.payment.application;

import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentProduct;

public interface PaymentGateway {
    Payment createPayment(String key, PaymentProduct product);

    void processAfterPaid(ProductPayRequest request);
}
