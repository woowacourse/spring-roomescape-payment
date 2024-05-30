package roomescape.infra;

import roomescape.domain.Payment;
import roomescape.dto.PaymentRequest;

public interface PaymentRestClient {

    Payment requestPaymentApproval(PaymentRequest request);
}
