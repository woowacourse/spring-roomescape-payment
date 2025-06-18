package roomescape.payment.domain;

import roomescape.payment.application.dto.PaymentGatewayRequest;
import roomescape.payment.application.dto.PaymentGatewayResponse;

public interface PaymentGateway {

    PaymentGatewayResponse approvePayment(final PaymentGatewayRequest paymentGatewayRequest);
}
