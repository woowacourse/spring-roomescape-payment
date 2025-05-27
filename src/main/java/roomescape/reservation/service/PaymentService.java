package roomescape.reservation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import roomescape.reservation.PaymentRestClient;
import roomescape.reservation.entity.Payment;
import roomescape.reservation.repository.PaymentRepository;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRestClient paymentRestClient;
    private final PaymentRepository paymentRepository;

    public void create(Payment payment) {
        paymentRestClient.approve(payment);
        paymentRepository.save(payment);
    }
}
