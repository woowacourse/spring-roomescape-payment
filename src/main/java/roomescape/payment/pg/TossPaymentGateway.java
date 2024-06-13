package roomescape.payment.pg;

import org.springframework.stereotype.Component;
import roomescape.global.exception.ViolationException;
import roomescape.payment.application.PaymentGateway;
import roomescape.payment.application.ProductPayRequest;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentProduct;

@Component
class TossPaymentGateway implements PaymentGateway {
    private final TossPaymentsClient client;

    public TossPaymentGateway(TossPaymentsClient client) {
        this.client = client;
    }

    @Override
    public Payment createPayment(String key, PaymentProduct product) {
        TossPaymentsPayment payment = client.findBy(key);

        return new Payment(payment.paymentKey(), payment.orderId(), payment.totalAmount(), product);
    }

    @Override
    public void processAfterPaid(ProductPayRequest request) {
        TossPaymentsConfirmRequest tossRequest = new TossPaymentsConfirmRequest(request);
        TossPaymentsPayment payment = client.findBy(request.paymentKey());
        boolean verified = payment.verify(tossRequest);
        if (!verified) {
            throw new ViolationException("올바른 결제 정보를 입력해주세요.");
        }

        client.confirm(tossRequest);
    }
}
