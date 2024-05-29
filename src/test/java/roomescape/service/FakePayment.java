package roomescape.service;

import org.springframework.http.HttpStatusCode;
import roomescape.dto.PaymentErrorResponse;
import roomescape.dto.PaymentRequest;
import roomescape.exception.PaymentException;

public class FakePayment implements PaymentClient {
    public static final PaymentRequest CORRECT_REQ = new PaymentRequest(
            "truePaymentKey", "trueOrderId", 1
    );
    public static final PaymentRequest WRONG_REQ = new PaymentRequest(
            "wrongPaymentKey", "wrongOrderId", 0
    );

    @Override
    public void pay(PaymentRequest paymentRequest) {
        if (CORRECT_REQ.paymentKey().equals(paymentRequest.paymentKey())
                && CORRECT_REQ.orderId().equals(paymentRequest.orderId())
                && CORRECT_REQ.amount() == paymentRequest.amount()) {
            return;
        }
        throw new PaymentException(new PaymentErrorResponse("BAD_REQUEST", "잘못된 요청입니다."),
                HttpStatusCode.valueOf(400));
    }
}
