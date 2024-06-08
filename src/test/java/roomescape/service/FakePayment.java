package roomescape.service;

import org.springframework.http.HttpStatusCode;
import roomescape.domain.CancelReason;
import roomescape.domain.Payment;
import roomescape.exception.PaymentException;

public class FakePayment implements PaymentClient {
    public static final Payment CORRECT_REQ = new Payment(
            "truePaymentKey", "trueOrderId", 1
    );
    public static final Payment WRONG_REQ = new Payment(
            "wrongPaymentKey", "wrongOrderId", 0
    );

    @Override
    public void pay(Payment payment) {
        if (CORRECT_REQ.getPaymentKey().equals(payment.getPaymentKey())
                && CORRECT_REQ.getOrderId().equals(payment.getOrderId())
                && CORRECT_REQ.getAmount() == payment.getAmount()) {
            return;
        }
        throw new PaymentException(HttpStatusCode.valueOf(400), "[테스트] 잘못된 요청입니다.");
    }

    @Override
    public void cancel(Payment payment, CancelReason cancelReason) {
        if (CORRECT_REQ.getPaymentKey().equals(payment.getPaymentKey())
                && CORRECT_REQ.getOrderId().equals(payment.getOrderId())
                && CORRECT_REQ.getAmount() == payment.getAmount()) {
            return;
        }
        throw new PaymentException(HttpStatusCode.valueOf(400), "[테스트] 잘못된 요청입니다.");
    }
}
