package roomescape.payment.service;

import org.springframework.stereotype.Service;
import roomescape.client.TossPaymentsClient;
import roomescape.client.dto.PaymentsConfirmRequest;
import roomescape.client.dto.PaymentsConfirmResponse;
import roomescape.payment.domain.Payment;
import roomescape.payment.repository.PaymentRepository;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final TossPaymentsClient tossPaymentsClient;

    public PaymentService(PaymentRepository paymentRepository, TossPaymentsClient tossPaymentsClient) {
        this.paymentRepository = paymentRepository;
        this.tossPaymentsClient = tossPaymentsClient;
    }

    public Payment confirmAndSavePayment(final PaymentsConfirmRequest request) {
        final PaymentsConfirmResponse paymentsConfirmResponse = tossPaymentsClient.confirmPayments(request);
        final Payment payment = new Payment(paymentsConfirmResponse.paymentKey(),
                paymentsConfirmResponse.totalAmount());
        return paymentRepository.save(payment);
    }
}
