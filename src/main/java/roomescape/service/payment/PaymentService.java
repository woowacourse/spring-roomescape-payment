package roomescape.service.payment;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.dto.PaymentCancelRequest;
import roomescape.domain.dto.PaymentRequest;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentClient;
import roomescape.domain.payment.PaymentRepository;

@Service
@Transactional(readOnly = true)
public class PaymentService {

    private static final String CANCEL_REASON = "고객변심";
    private final PaymentRepository paymentRepository;
    private final PaymentClient paymentClient;

    public PaymentService(PaymentRepository paymentRepository, PaymentClient paymentClient) {
        this.paymentRepository = paymentRepository;
        this.paymentClient = paymentClient;
    }

    @Transactional
    public Payment approvePayment(PaymentRequest request) {
        Payment payment = paymentClient.approve(request);

        return paymentRepository.save(payment);
    }

    @Transactional
    public void cancelPayment(Payment payment) {
        paymentClient.cancel(new PaymentCancelRequest(payment.getPaymentKey(), CANCEL_REASON));
    }
}
