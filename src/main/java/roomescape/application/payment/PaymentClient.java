package roomescape.application.payment;

import roomescape.application.payment.dto.Payment;
import roomescape.application.payment.dto.request.PaymentRequest;

public interface PaymentClient {

    Payment requestPurchase(PaymentRequest request);
}
