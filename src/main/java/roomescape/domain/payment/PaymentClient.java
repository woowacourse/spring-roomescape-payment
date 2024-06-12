package roomescape.domain.payment;

import roomescape.domain.dto.PaymentCancelRequest;
import roomescape.domain.dto.PaymentRequest;

public interface PaymentClient {
    Payment approve(PaymentRequest request);

    void cancel(PaymentCancelRequest request);
}
