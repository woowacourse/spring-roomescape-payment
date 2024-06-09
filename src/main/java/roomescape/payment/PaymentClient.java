package roomescape.payment;

import roomescape.service.dto.request.PaymentRequest;

public interface PaymentClient {

    TossPaymentResponse confirm(PaymentRequest paymentRequest);
}
