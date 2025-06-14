package roomescape.payment;

import roomescape.payment.domain.PaymentType;
import roomescape.payment.dto.TossPaymentRequest;
import roomescape.payment.dto.TossPaymentResponse;
import roomescape.payment.toss.TossPaymentClient;

public class FakeTossPaymentClient implements TossPaymentClient {

    @Override
    public TossPaymentResponse requestPaymentApprove(TossPaymentRequest request) {
        return new TossPaymentResponse("test", "test", 1L, PaymentType.NORMAL);
    }
}
