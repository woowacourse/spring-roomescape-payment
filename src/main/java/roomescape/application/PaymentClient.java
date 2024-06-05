package roomescape.application;

import roomescape.application.dto.request.PaymentConfirmApiRequest;
import roomescape.application.dto.response.PaymentConfirmApiResponse;

public interface PaymentClient {

    PaymentConfirmApiResponse confirmPayment(PaymentConfirmApiRequest paymentConfirmApiRequest);
}
