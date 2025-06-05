package roomescape.payment.service;

import roomescape.payment.dto.response.PaymentResponse;
import roomescape.reservation.dto.request.PaymentRequest;

public interface PaymentApiClient {

    PaymentResponse authPayment(PaymentRequest payment);
}
