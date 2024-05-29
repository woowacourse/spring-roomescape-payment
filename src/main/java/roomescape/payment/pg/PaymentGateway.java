package roomescape.payment.pg;

import roomescape.payment.dto.PaymentConfirmResponse;

public interface PaymentGateway {

    PaymentConfirmResponse confirm(String orderId, Long amount, String paymentKey);
}
