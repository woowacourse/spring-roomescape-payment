package roomescape.support;

import roomescape.payment.PaymentClient;
import roomescape.service.dto.request.PaymentRequest;
import roomescape.service.dto.response.PaymentResponse;

public class FakePaymentClient implements PaymentClient {

    @Override
    public PaymentResponse confirm(PaymentRequest paymentRequest) {
        return null; // TOOD: 테스트 코드 수정하면서 null 제거
    }
}
