package roomescape.service;

import roomescape.domain.CancelReason;
import roomescape.domain.Payment;

public interface PaymentClient {

    void pay(Payment payment);

    void cancel(Payment payment, CancelReason cancelReason);
}
