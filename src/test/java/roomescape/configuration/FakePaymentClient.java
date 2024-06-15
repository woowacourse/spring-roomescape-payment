package roomescape.configuration;

import roomescape.domain.PaymentInfo;
import roomescape.dto.request.MemberReservationRequest;
import roomescape.infrastructure.PaymentClient;

public class FakePaymentClient implements PaymentClient {

    @Override
    public PaymentInfo payment(MemberReservationRequest memberReservationRequest) {
        return new PaymentInfo(memberReservationRequest.amount(), memberReservationRequest.orderId(),
                memberReservationRequest.paymentKey());
    }
}
