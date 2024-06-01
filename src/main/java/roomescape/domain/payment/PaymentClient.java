package roomescape.domain.payment;

import roomescape.service.dto.PaymentRequest;

public interface PaymentClient {

    void requestApproval(PaymentRequest request);
}
