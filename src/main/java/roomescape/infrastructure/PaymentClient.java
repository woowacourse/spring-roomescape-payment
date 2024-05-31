package roomescape.infrastructure;

import roomescape.service.dto.PaymentRequest;

public interface PaymentClient {

    void confirm(PaymentRequest request);
}
