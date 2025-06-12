package roomescape.application;

import org.springframework.stereotype.Service;
import roomescape.application.request.PaymentInfo;
import roomescape.application.response.PaymentResponse;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentRepository;
import roomescape.infrastructure.payment.PaymentClient;

@Service
public class PaymentService {

    private final PaymentClient paymentClient;
    private final PaymentRepository paymentRepository;

    public PaymentService(final PaymentClient paymentClient, final PaymentRepository paymentRepository) {
        this.paymentClient = paymentClient;
        this.paymentRepository = paymentRepository;
    }

    public Payment savePayment(final PaymentInfo paymentInfo) {
        PaymentResponse response = paymentClient.confirmPayment(paymentInfo);
        Payment payment = Payment.register(response.orderId(), response.paymentKey(), response.orderName(),
                response.totalAmount());

        return paymentRepository.save(payment);
    }
}
