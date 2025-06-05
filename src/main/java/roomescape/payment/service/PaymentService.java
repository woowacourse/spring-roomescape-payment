package roomescape.payment.service;

import org.springframework.stereotype.Service;
import roomescape.payment.domain.Payment;
import roomescape.payment.dto.response.PaymentResponse;
import roomescape.payment.repository.PaymentRepository;
import roomescape.reservation.domain.Reservation;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(final PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }


    public void create(final PaymentResponse response, final Reservation reservation) {
        Payment payment = new Payment(reservation, response.paymentKey(), response.orderId(), response.type(),
                response.totalAmount(), response.status(), response.requestedAt());

        paymentRepository.save(payment);
    }
}
