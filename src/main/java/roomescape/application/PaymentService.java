package roomescape.application;

import org.springframework.stereotype.Service;
import roomescape.application.dto.PaymentProcessRequest;
import roomescape.config.PaymentRestClient;
import roomescape.domain.Payment;
import roomescape.infrastructure.repository.PaymentRepository;

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

    public Payment process(PaymentProcessRequest request) {
        Payment payment = paymentRestClient.getPayment(request);
        return paymentRepository.save(payment);
    }
}
