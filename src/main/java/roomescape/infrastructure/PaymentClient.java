package roomescape.infrastructure;

import roomescape.service.dto.PaymentRequest;

public interface PaymentClient {

    void requestApproval(PaymentRequest request);
}
