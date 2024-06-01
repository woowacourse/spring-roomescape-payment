package roomescape.payment;

import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.annotation.Primary;
import roomescape.common.exception.ClientException;
import roomescape.payment.client.PaymentClient;
import roomescape.payment.dto.request.ConfirmPaymentRequest;
import roomescape.payment.model.Payment;

@TestComponent
@Primary
public class FakePaymentClient implements PaymentClient {
    private final static String FAIL_KEYWORD = "failPayment";

    @Override
    public Payment confirm(final ConfirmPaymentRequest confirmPaymentRequest) {
        if (confirmPaymentRequest.paymentKey().equals(FAIL_KEYWORD)) {
            throw new ClientException("결제 오류입니다. 같은 문제가 반복된다면 문의해주세요.");
        }

        return new Payment(confirmPaymentRequest.paymentKey(), confirmPaymentRequest.orderId(),
                confirmPaymentRequest.amount());
    }
}
