package roomescape.payment.service;

import org.springframework.stereotype.Service;
import roomescape.payment.client.TossPayRestClient;
import roomescape.payment.domain.Payment;
import roomescape.payment.dto.PaymentRequest;
import roomescape.payment.dto.PaymentResponse;
import roomescape.payment.repository.PaymentRepository;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final TossPayRestClient tossPayRestClient;

    public PaymentService(PaymentRepository paymentRepository, TossPayRestClient tossPayRestClient) {
        this.paymentRepository = paymentRepository;
        this.tossPayRestClient = tossPayRestClient;
    }

    public PaymentResponse pay(PaymentRequest paymentRequest, Long reservationId) {
        Payment payment = tossPayRestClient.pay(paymentRequest);
        payment.bindToReservation(reservationId);
        Payment savedPayment = paymentRepository.save(payment);

        return PaymentResponse.toResponse(savedPayment);
    }
}
