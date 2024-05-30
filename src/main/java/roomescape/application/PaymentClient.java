package roomescape.application;

import roomescape.application.dto.request.PaymentApiRequest;
import roomescape.application.dto.response.PaymentApiResponse;

public interface PaymentClient {

    PaymentApiResponse confirmPayment(PaymentApiRequest paymentApiRequest);
}
