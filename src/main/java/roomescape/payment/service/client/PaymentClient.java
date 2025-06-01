package roomescape.payment.service.client;

import roomescape.payment.service.dto.PaymentConfirmRequest;
import roomescape.payment.service.dto.PaymentClientResponse;

public interface PaymentClient {

    PaymentClientResponse completePayment(PaymentConfirmRequest paymentConfirmRequest);
}
