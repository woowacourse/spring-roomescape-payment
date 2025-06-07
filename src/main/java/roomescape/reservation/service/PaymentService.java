package roomescape.reservation.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.reservation.entity.Payment;
import roomescape.reservation.entity.Reservation;
import roomescape.reservation.external.client.PaymentRestClient;
import roomescape.reservation.repository.PaymentRepository;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRestClient paymentRestClient;
    private final PaymentRepository paymentRepository;

    @Transactional
    public void create(Payment payment) {
        paymentRepository.save(payment);
        paymentRestClient.approve(payment);
    }

    public List<Payment> findAllByReservations(List<Reservation> reservations) {
        return paymentRepository.findAllByReservationIn(reservations);
    }
}
