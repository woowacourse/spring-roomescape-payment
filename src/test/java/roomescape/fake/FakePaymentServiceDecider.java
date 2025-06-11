package roomescape.fake;

import roomescape.reservation.domain.PaymentType;
import roomescape.reservation.service.PaymentService;
import roomescape.reservation.service.PaymentServiceDecider;
import roomescape.reservation.service.dto.PaymentRequest;

public class FakePaymentServiceDecider implements PaymentServiceDecider {

    @Override
    public PaymentService<PaymentRequest> decide(PaymentType type) {
        return new FakePaymentService();
    }
}
