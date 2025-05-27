package roomescape.payment.service;

import org.springframework.stereotype.Service;
import roomescape.payment.processor.toss.TossPaymentConfirmRequest;
import roomescape.payment.processor.toss.TossPaymentConfirmResponse;
import roomescape.payment.processor.toss.TossPaymentProcessor;

@Service
public class PaymentService {

    private final TossPaymentProcessor tossPaymentProcessor;

    public PaymentService(TossPaymentProcessor tossPaymentProcessor) {
        this.tossPaymentProcessor = tossPaymentProcessor;
    }

    public TossPaymentConfirmResponse processPayment(
            final String paymentKey,
            final String orderId,
            final int amount
    ) {
        return tossPaymentProcessor.processPayment(new TossPaymentConfirmRequest(amount, orderId, paymentKey));
    }
}
