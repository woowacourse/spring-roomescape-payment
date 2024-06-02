package roomescape.infra;

import roomescape.domain.Payment;
import roomescape.dto.PaymentRequest;

public interface PaymentClient {
    Payment requestPaymentApproval(PaymentRequest request);
}
