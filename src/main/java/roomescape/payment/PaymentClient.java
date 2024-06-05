package roomescape.payment;

import roomescape.service.dto.request.PaymentRequest;

public interface PaymentClient {

    void confirm(PaymentRequest paymentRequest);
}
