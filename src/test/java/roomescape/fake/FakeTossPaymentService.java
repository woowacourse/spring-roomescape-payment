package roomescape.fake;

import roomescape.reservation.external.toss.TossPaymentResponse;
import roomescape.reservation.external.toss.TossPaymentService;
import roomescape.reservation.external.toss.TossPaymentRequest;

public class FakeTossPaymentService implements TossPaymentService {

    public TossPaymentResponse paymentReservation(final TossPaymentRequest request) {
        return new TossPaymentResponse(request.orderId(), request.amount(), request.paymentKey());
    }
}
