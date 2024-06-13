package roomescape.service.client;

import roomescape.service.dto.PaymentRequest;

public interface PaymentClient {
    void requestPayment(PaymentRequest body);
}
