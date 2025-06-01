package roomescape.service.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.Payment;
import roomescape.dto.business.PaymentHistoryCreationContent;
import roomescape.dto.business.PaymentResult;
import roomescape.repository.PaymentRepository;
import roomescape.utility.payment.PaymentClient;

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

    public Payment savePayment(PaymentHistoryCreationContent content) {
        PaymentResult paymentResult = requestPaymentAuthorization(content);
        Payment payment = Payment.createWithoutId(
                paymentResult.orderId(), paymentResult.paymentKey(), content.paymentType());
        return paymentRepository.save(payment);
    }

    private PaymentResult requestPaymentAuthorization(PaymentHistoryCreationContent content) {
        return paymentClient.authorizePayment(content.paymentKey(), content.orderId(), content.amount());
    }
}
