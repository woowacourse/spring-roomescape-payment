package roomescape.application.payment;

import roomescape.application.payment.dto.PaymentClientRequest;
import roomescape.domain.payment.Payment;

public interface PaymentClient {

    Payment requestPurchase(PaymentClientRequest request);
}
