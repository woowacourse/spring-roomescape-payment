package roomescape.reservation.service.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import roomescape.payment.domain.Payment;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationPayment;
import roomescape.reservation.repository.ReservationPaymentRepository;

@RequiredArgsConstructor
@Service
public class ReservationPaymentCommandUseCase {

    private final ReservationPaymentRepository reservationPaymentRepository;

    public void save(
            final Reservation reservation,
            final Payment payment
    ) {
        final ReservationPayment reservationPayment = new ReservationPayment(payment, reservation);

        reservationPaymentRepository.save(reservationPayment);
    }
}
