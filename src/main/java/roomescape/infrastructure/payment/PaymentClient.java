package roomescape.infrastructure.payment;

import roomescape.application.request.PaymentInfo;
import roomescape.application.response.PaymentClientResponse;

public interface PaymentClient {

    PaymentClientResponse confirmPayment(PaymentInfo paymentInfo);
}
