package roomescape.service.payment;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentRepository;
import roomescape.service.payment.dto.PaymentRequest;
import roomescape.service.payment.dto.PaymentResult;

@Service
public class PaymentService {
    private final TossPaymentClient paymentClient;
    private final PaymentRepository paymentRepository;

    public PaymentService(TossPaymentClient paymentClient, PaymentRepository paymentRepository) {
        this.paymentClient = paymentClient;
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public Payment confirm(PaymentRequest paymentRequest) {
        PaymentResult paymentResult = paymentClient.confirm(paymentRequest);
        return paymentRepository.save(
                new Payment(
                        paymentResult.orderId(), paymentResult.paymentKey(),
                        paymentResult.totalAmount(), paymentResult.type()
                )
        );
    }

    @Transactional
    public void cancel(Payment payment) {
        paymentRepository.deleteById(payment.getId());
        paymentClient.cancel(payment);
    }
}
