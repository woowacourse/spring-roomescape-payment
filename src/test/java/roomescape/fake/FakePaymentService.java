package roomescape.fake;

import roomescape.reservation.domain.PaymentType;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.service.PaymentService;
import roomescape.reservation.service.dto.PaymentRequest;

public class FakePaymentService implements PaymentService<PaymentRequest> {

    @Override
    public void createPayment(final PaymentRequest request, final Reservation reservation) {

    }

    @Override
    public PaymentType getType() {
        return null;
    }
}
