package roomescape.fake;

import roomescape.reservation.external.toss.TossPaymentRequest;
import roomescape.reservation.external.toss.TossPaymentResponse;
import roomescape.reservation.external.toss.TossApiClient;

public class FakeTossApiClient extends TossApiClient {

    public FakeTossApiClient() {
        super(null, null);
    }

    public TossPaymentResponse requestPayment(final TossPaymentRequest tossPaymentRequest) {
        return new TossPaymentResponse(tossPaymentRequest.orderId(), tossPaymentRequest.amount(),
                tossPaymentRequest.paymentKey());
    }
}
