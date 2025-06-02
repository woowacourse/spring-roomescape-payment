package roomescape.application;

import org.springframework.stereotype.Service;
import roomescape.domain.Payment;
import roomescape.infrastructure.repository.PaymentRepository;
import roomescape.infrastructure.thirdparty.PaymentRestClient;
import roomescape.infrastructure.thirdparty.dto.PaymentConfirmResponse;
import roomescape.presentation.dto.request.PaymentProcessRequest;

@Service
public class PaymentService {

    private final PaymentRestClient paymentRestClient;
    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRestClient paymentRestClient,
                          PaymentRepository paymentRepository
    ) {
        this.paymentRestClient = paymentRestClient;
        this.paymentRepository = paymentRepository;
    }

    public Payment processPayment(PaymentProcessRequest request) {
        PaymentConfirmResponse response = paymentRestClient.getPaymentResponse(request);
        Payment payment = Payment.create(response.paymentKey(), request.orderId());
        return paymentRepository.save(payment);
    }
}
