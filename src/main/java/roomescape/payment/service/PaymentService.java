package roomescape.payment.service;

import org.springframework.stereotype.Service;
import roomescape.client.PaymentClient;
import roomescape.client.dto.PaymentsConfirmRequest;
import roomescape.client.dto.PaymentsConfirmResponse;
import roomescape.payment.domain.Payment;
import roomescape.payment.repository.PaymentRepository;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentClient paymentClient;

    public PaymentService(final PaymentRepository paymentRepository, final PaymentClient paymentClient) {
        this.paymentRepository = paymentRepository;
        this.paymentClient = paymentClient;
    }

    public Payment confirmAndSavePayment(final PaymentsConfirmRequest request) {
        final PaymentsConfirmResponse paymentsConfirmResponse = paymentClient.confirmPayments(request);
        final Payment payment = new Payment(paymentsConfirmResponse.paymentKey(),
                paymentsConfirmResponse.totalAmount());
        return paymentRepository.save(payment);
    }
}
