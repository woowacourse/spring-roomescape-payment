package roomescape.support;

import roomescape.application.dto.request.payment.PaymentRequest;
import roomescape.application.dto.response.payment.PaymentResponse;
import roomescape.domain.payment.CancelReason;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentClient;
import roomescape.exception.PaymentClientException;

public class FakePaymentClient implements PaymentClient {
    private static final String INVALID_PAYMENT_KEY = "invalidPaymentKey";

    public static String getInvalidPaymentKey() {
        return INVALID_PAYMENT_KEY;
    }

    @Override
    public PaymentResponse confirmPayment(PaymentRequest paymentRequest) {
        if (paymentRequest.paymentKey().equals(INVALID_PAYMENT_KEY)) {
            throw new PaymentClientException("잘못된 요청입니다");
        }

        return PaymentResponse.empty();
    }

    @Override
    public void cancel(Payment payment, CancelReason request) {
        if (payment.getPaymentKey().equals(INVALID_PAYMENT_KEY)) {
            throw new PaymentClientException("잘못된 요청입니다");
        }
    }
}
