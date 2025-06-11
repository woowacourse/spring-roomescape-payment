package roomescape.domain.payment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import roomescape.cllient.payment.PaymentClient;
import roomescape.domain.payment.domain.Payment;
import roomescape.domain.payment.dto.PaymentCreationContent;
import roomescape.domain.payment.dto.PaymentResult;
import roomescape.domain.payment.repository.PaymentRepository;

@Service
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentClient paymentClient;

    public PaymentService(
            PaymentRepository paymentRepository,
            PaymentClient paymentClient
    ) {
        this.paymentRepository = paymentRepository;
        this.paymentClient = paymentClient;
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public Payment savePayment(PaymentCreationContent content) {
        PaymentResult paymentResult = requestPaymentAuthorization(content);
        Payment payment = Payment.createWithoutId(
                paymentResult.orderId(), paymentResult.paymentKey(), paymentResult.totalAmount());
        return paymentRepository.save(payment);
    }

    private PaymentResult requestPaymentAuthorization(PaymentCreationContent content) {
        return paymentClient.authorizePayment(content.paymentKey(), content.orderId(), content.amount());
    }
}
