package roomescape.payment;

import roomescape.service.dto.request.PaymentRequest;

public interface PaymentClient {

    PaymentResponse confirm(PaymentRequest paymentRequest);
}
