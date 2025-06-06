package roomescape.payment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.payment.domain.Payment;
import roomescape.payment.processor.toss.TossPaymentConfirmRequest;
import roomescape.payment.processor.toss.TossPaymentConfirmResponse;
import roomescape.payment.processor.toss.TossPaymentProcessor;
import roomescape.payment.repository.PaymentRepositoryInterface;

@Service
public class PaymentService {

    private final PaymentRepositoryInterface paymentRepository;
    private final TossPaymentProcessor tossPaymentProcessor;

    public PaymentService(
            final PaymentRepositoryInterface paymentRepository,
            final TossPaymentProcessor tossPaymentProcessor
    ) {
        this.paymentRepository = paymentRepository;
        this.tossPaymentProcessor = tossPaymentProcessor;
    }

    @Transactional
    public Payment processPayment(
            final int amount,
            final String orderId,
            final String paymentKey
    ) {
        final TossPaymentConfirmResponse tossPaymentConfirmResponse =
                tossPaymentProcessor.processPayment(new TossPaymentConfirmRequest(amount, orderId, paymentKey));

        final Payment payment = new Payment(
                amount,
                tossPaymentConfirmResponse.orderId(),
                tossPaymentConfirmResponse.paymentKey()
        );

        return paymentRepository.save(payment);
    }
}
