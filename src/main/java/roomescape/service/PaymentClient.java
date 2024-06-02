package roomescape.service;

import roomescape.dto.PaymentRequest;

public interface PaymentClient {

    void pay(PaymentRequest paymentRequest);
}
