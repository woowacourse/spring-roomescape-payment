package roomescape.service;

import roomescape.service.dto.request.PaymentRequest;

public interface PaymentClient {

    void confirm(PaymentRequest paymentRequest);
}
