package roomescape.reservation.service;

import java.util.Optional;
import org.springframework.stereotype.Service;
import roomescape.reservation.domain.Payment;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationPayment;
import roomescape.reservation.repository.ReservationPaymentRepository;

@Service
public class ReservationPaymentService {
    
    private final ReservationPaymentRepository reservationPaymentRepository;

    public ReservationPaymentService(ReservationPaymentRepository reservationPaymentRepository) {
        this.reservationPaymentRepository = reservationPaymentRepository;
    }

    public void save(Reservation reservation, Payment payment) {
        reservationPaymentRepository.save(new ReservationPayment(reservation, payment));
    }

    public Optional<Payment> findPaymentByReservationId(Long id) {
        return reservationPaymentRepository.findPaymentByReservationId(id);
    }
}
