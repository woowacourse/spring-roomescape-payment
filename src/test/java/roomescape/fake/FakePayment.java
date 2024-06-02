package roomescape.fake;

import java.time.LocalDateTime;
import roomescape.application.dto.request.payment.PaymentRequest;
import roomescape.application.dto.response.payment.PaymentResponse;
import roomescape.domain.payment.CancelReason;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentClient;

public class FakePayment implements PaymentClient {
    public static final int AMOUNT = 1000;
    public static final String ORDER_ID = "orderId";
    public static final String PAYMENT_KEY = "paymentKey";

    @Override
    public void cancel(Payment payment, CancelReason request) {
    }

    @Override
    public PaymentResponse confirm(PaymentRequest paymentRequest) {
        LocalDateTime requestedAt = LocalDateTime.now();
        LocalDateTime approvedAt = requestedAt.plusSeconds(1);
        return new PaymentResponse(AMOUNT, PAYMENT_KEY, ORDER_ID, requestedAt.toString(), approvedAt.toString());
    }
}
