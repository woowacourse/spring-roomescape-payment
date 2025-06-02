package roomescape.payment.service;

import roomescape.payment.dto.response.PaymentResponse;

public interface ApiClient {

    PaymentResponse authPayment(final String paymentKey, final String orderId,
                                final Integer amount, final String paymentType);
}
