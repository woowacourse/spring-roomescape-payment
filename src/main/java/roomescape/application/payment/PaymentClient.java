package roomescape.application.payment;

import roomescape.application.payment.dto.PaymentRequest;
import roomescape.domain.payment.Payment;

public interface PaymentClient {

    Payment requestPurchase(PaymentRequest request);
}
