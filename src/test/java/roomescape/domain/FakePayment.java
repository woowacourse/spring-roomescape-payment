package roomescape.domain;

import static roomescape.api.TossPaymentExceptionType.INVALID_REQUEST;

import roomescape.dto.PaymentRequest;
import roomescape.dto.PaymentResponse;
import roomescape.exception.PaymentException;

public class FakePayment implements PaymentClient {
    public static final PaymentRequest CORRECT_REQ = new PaymentRequest(
            "truePaymentKey", "trueOrderId", 1
    );
    public static final PaymentRequest WRONG_REQ = new PaymentRequest(
            "wrongPaymentKey", "wrongOrderId", 0
    );

    @Override
    public PaymentResponse pay(PaymentRequest paymentRequest) {
        if (CORRECT_REQ.paymentKey().equals(paymentRequest.paymentKey())
                && CORRECT_REQ.orderId().equals(paymentRequest.orderId())
                && CORRECT_REQ.amount() == paymentRequest.amount()) {
            return new PaymentResponse(paymentRequest.paymentKey(),
                    paymentRequest.orderId(),
                    paymentRequest.amount());
        }
        throw new PaymentException(INVALID_REQUEST.getHttpStatus(), INVALID_REQUEST.getMessage(),
                new RuntimeException());
    }
}
