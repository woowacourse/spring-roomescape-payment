package roomescape.service.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.Payment;
import roomescape.dto.business.PaymentCreationContent;
import roomescape.dto.business.PaymentResult;
import roomescape.external.payment.PaymentClient;
import roomescape.repository.PaymentRepository;

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
