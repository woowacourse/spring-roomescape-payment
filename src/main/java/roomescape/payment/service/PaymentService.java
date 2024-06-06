package roomescape.payment.service;

import org.springframework.stereotype.Service;
import roomescape.payment.client.PaymentClient;
import roomescape.payment.dto.request.ConfirmPaymentRequest;
import roomescape.payment.model.Payment;
import roomescape.payment.repository.PaymentRepository;

@Service
public class PaymentService {

    private final PaymentClient paymentClient;
    private final PaymentRepository paymentRepository;

    public PaymentService(final PaymentClient paymentClient,
                          final PaymentRepository paymentRepository) {
        this.paymentClient = paymentClient;
        this.paymentRepository = paymentRepository;
    }

    public Payment confirm(ConfirmPaymentRequest confirmPaymentRequest) {
        Payment payment = paymentClient.confirm(confirmPaymentRequest);
        return paymentRepository.save(payment);
    }
}
