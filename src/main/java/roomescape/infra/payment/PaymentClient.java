package roomescape.infra.payment;

import roomescape.application.dto.request.PaymentRequest;

public interface PaymentClient {
    void confirmPayment(PaymentRequest paymentRequest);
}
