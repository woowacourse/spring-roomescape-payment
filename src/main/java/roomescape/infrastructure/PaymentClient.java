package roomescape.infrastructure;

import roomescape.domain.payment.PaymentResponse;
import roomescape.service.dto.PaymentConfirmRequest;

public interface PaymentClient {

    PaymentResponse confirmPayment(PaymentConfirmRequest request);
}
