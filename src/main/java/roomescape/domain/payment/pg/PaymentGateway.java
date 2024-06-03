package roomescape.domain.payment.pg;

import roomescape.domain.payment.dto.PaymentConfirmResponse;

public interface PaymentGateway {

    PaymentConfirmResponse confirm(String orderId, Long amount, String paymentKey);
}
