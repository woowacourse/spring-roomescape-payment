package roomescape.service;

import roomescape.domain.Payment;

public interface PaymentClient {

    void pay(Payment payment);
}
