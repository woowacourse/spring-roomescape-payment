package roomescape.domain.payment;

import roomescape.domain.dto.PaymentRequest;

public interface PaymentClient {

    Payment approve(PaymentRequest request);

    void cancel(String paymentKey);
}
