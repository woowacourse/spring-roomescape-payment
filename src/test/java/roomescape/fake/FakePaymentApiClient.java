package roomescape.fake;

import roomescape.reservation.domain.Amount;
import roomescape.reservation.domain.OrderId;
import roomescape.reservation.domain.PaymentInfo;
import roomescape.reservation.domain.PaymentKey;
import roomescape.reservation.external.toss.dto.PaymentConfirmRequest;
import roomescape.reservation.service.PaymentApiClient;

public class FakePaymentApiClient implements PaymentApiClient {

    @Override
    public PaymentInfo paymentReservation(final PaymentConfirmRequest request) {
        return new PaymentInfo(
                new OrderId(request.orderId()),
                new Amount(request.amount()),
                new PaymentKey(request.paymentKey())
        );
    }
}
